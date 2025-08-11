import AnalyticsServiceInterface
import AssetsLibrary
import AvatarServiceInterface
import ComposableArchitecture
import FeatureActionLibrary
import FramesRepositoryInterface
import GearRepositoryInterface
import ModelsLibrary
import RecentlyUsedServiceInterface
import StringsLibrary
import SwiftUI
import ViewsLibrary

@Reducer
public struct RollEditorNext: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		@Shared(.frames) public var frames: [Frame.Edit]?
		@Shared(.currentFrame) public var currentFrame: Frame.Selection
		@Shared(.isEditable) public var isEditable: Bool

		public var recentGear: IdentifiedArrayOf<Gear.Summary> = []

		var ballRolled: Gear.Summary? {
			frames?[currentFrame.frameIndex].rolls[currentFrame.rollIndex].bowlingBall
		}

		var didFoul: Bool {
			frames?[currentFrame.frameIndex].rolls[currentFrame.rollIndex].roll.didFoul ?? false
		}

		fileprivate mutating func updateBallRolled(to: Gear.Summary?) {
			$frames.withLock {
				$0?[currentFrame.frameIndex].setBowlingBall(to, forRoll: currentFrame.rollIndex)
			}
		}

		fileprivate mutating func toggleDidFoul() {
			$frames.withLock {
				$0?[currentFrame.frameIndex].toggleDidFoul(forRoll: currentFrame.rollIndex)
			}
		}
	}

	public enum Action: FeatureAction, ViewAction {
		@CasePathable
		public enum View {
			case task
			case didTapBall(Gear.ID)
			case didTapOtherButton
			case didToggleFoul
		}
		@CasePathable
		public enum Internal {
			case didLoadGear(Result<[Gear.Summary], Error>)
		}
		@CasePathable
		public enum Delegate {
			case didRequestBallPicker
			case didProvokeLock
		}

		case view(View)
		case `internal`(Internal)
		case delegate(Delegate)
	}

	@Dependency(GearRepository.self) var gear
	@Dependency(RecentlyUsedService.self) var recentlyUsed

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .task:
					return .run { send in
						for try await recentlyUsed in gear.mostRecentlyUsed(ofKind: .bowlingBall, limit: 4) {
							await send(.internal(.didLoadGear(.success(recentlyUsed.sorted(by: { $0.name < $1.name })))))
						}
					} catch: { error, send in
						await send(.internal(.didLoadGear(.failure(error))))
					}

				case let .didTapBall(id):
					guard state.isEditable else { return .send(.delegate(.didProvokeLock)) }
					guard let ball = state.recentGear[id: id] else { return .send(.delegate(.didRequestBallPicker)) }

					if state.ballRolled?.id == id {
						state.updateBallRolled(to: nil)
						return .none
					} else {
						state.updateBallRolled(to: ball)
						return .run { [id = id] _ in recentlyUsed.didRecentlyUseResource(.gear, id) }
					}

				case .didToggleFoul:
					guard state.isEditable else { return .send(.delegate(.didProvokeLock)) }
					state.toggleDidFoul()
					return .none

				case .didTapOtherButton:
					guard state.isEditable else { return .send(.delegate(.didProvokeLock)) }
					return .send(.delegate(.didRequestBallPicker))
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .didLoadGear(.success(gear)):
					state.recentGear = .init(uniqueElements: gear)
					if let ballRolled = state.ballRolled {
						state.recentGear.append(ballRolled)
					}
					return .none

				case .didLoadGear(.failure):
					// FIXME: Should this error from loading recent gear be handled, or just let silently fail?
					return .none
				}

			case .delegate:
				return .none
			}
		}

		ErrorHandlerReducer<State, Action> { _, action in
			switch action {
			case let .internal(.didLoadGear(.failure(error))):
				return error
			default:
				return nil
			}
		}
	}
}

// MARK: - View

@ViewAction(for: RollEditorNext.self)
public struct RollEditorNextView: View {
	public let store: StoreOf<RollEditorNext>

	private static let selectedStrokeStyle = StrokeStyle(lineWidth: 4, lineCap: .round, dash: [8])

	public var body: some View {
		HStack(alignment: .bottom) {
			VStack(alignment: .leading, spacing: .unitSpacing) {
				Text(Strings.Roll.Properties.Ball.title)
					.font(.caption)
					.italic()
					.foregroundStyle(.white)

				HStack(spacing: .smallSpacing) {
					ForEach(store.recentGear) { gear in
						Button { send(.didTapBall(gear.id)) } label: {
							AvatarView(gear.avatar, size: .smallerIcon)
								.overlay(
									Circle()
										.stroke(gear.id == store.ballRolled?.id ? .white : .clear, style: Self.selectedStrokeStyle)
								)
								.opacity(gear.id == store.ballRolled?.id ? 1 : 0.8)
								.padding(.tinySpacing)
						}
					}

					Button { send(.didTapOtherButton) } label: {
						Image(systemName: "chevron.right.square")
							.resizable()
							.scaledToFit()
							.foregroundStyle(.white)
							.frame(width: .smallIcon, height: .smallIcon)
							.padding(.smallSpacing)
					}
				}
			}

			Spacer()

			Button { send(.didToggleFoul) } label: {
				HStack(spacing: .smallSpacing) {
					Text(Strings.Roll.Properties.Foul.title)
						.foregroundStyle(store.didFoul ? Asset.Colors.ScoreSheet.Plain.Text.OnBackground.foul.swiftUIColor : .white)
					Image(systemName: store.didFoul ? "f.cursive.circle.fill" : "f.cursive.circle")
						.resizable()
						.frame(width: .smallIcon, height: .smallIcon)
						.foregroundStyle(store.didFoul ? Asset.Colors.ScoreSheet.Plain.Text.OnBackground.foul.swiftUIColor : .white)
				}
			}
			.buttonStyle(TappableElement())
		}
		.task { await send(.task).finish() }
	}
}

extension Color {
	var rgb: Avatar.Background.RGB {
		let (red, green, blue, _) = UIColor(self).rgba
		return .init(red, green, blue)
	}
}
