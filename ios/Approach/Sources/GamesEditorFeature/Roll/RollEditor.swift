import AssetsLibrary
import AvatarServiceInterface
import ComposableArchitecture
import EquatableLibrary
import ExtensionsLibrary
import FeatureActionLibrary
import FeatureFlagsServiceInterface
import GearRepositoryInterface
import ModelsLibrary
import RecentlyUsedServiceInterface
import StringsLibrary
import SwiftUI
import ViewsLibrary

@Reducer
public struct RollEditor: Reducer {
	@ObservableState
	public struct State: Equatable {
		public var ballRolled: Gear.Summary?
		public var didFoul: Bool
		public var isEditable: Bool = true

		public var recentGear: IdentifiedArrayOf<Gear.Summary> = []

		init(ballRolled: Gear.Summary? = nil, didFoul: Bool = false) {
			self.ballRolled = ballRolled
			self.didFoul = didFoul
		}
	}

	public enum Action: FeatureAction, ViewAction {
		@CasePathable public enum View {
			case didStartTask
			case didTapBall(Gear.ID)
			case didTapOtherButton
			case didToggleFoul
		}
		@CasePathable public enum Internal {
			case didLoadGear(Result<[Gear.Summary], Error>)
		}
		@CasePathable public enum Delegate {
			case didEditRoll
			case didRequestBallPicker
			case didChangeBall(Gear.Summary?)
			case didProvokeLock
		}

		case view(View)
		case `internal`(Internal)
		case delegate(Delegate)
	}

	public init() {}

	@Dependency(GearRepository.self) var gear
	@Dependency(RecentlyUsedService.self) var recentlyUsed

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didStartTask:
					return .run { send in
						for try await gear in self.gear.mostRecentlyUsed(ofKind: .bowlingBall, limit: 4) {
							await send(.internal(.didLoadGear(.success(gear.sorted(by: { $0.name < $1.name })))))
						}
					} catch: { error, send in
						await send(.internal(.didLoadGear(.failure(error))))
					}

				case let .didTapBall(id):
					guard state.isEditable else { return .send(.delegate(.didProvokeLock)) }
					guard let ball = state.recentGear[id: id] else { return .send(.delegate(.didRequestBallPicker)) }
					if state.ballRolled?.id == id {
						state.ballRolled = nil
						return .send(.delegate(.didChangeBall(nil)))
					} else {
						state.ballRolled = ball
						return .merge(
							.send(.delegate(.didChangeBall(ball))),
							.run { [id = id] _ in recentlyUsed.didRecentlyUseResource(.gear, id) }
						)
					}

				case .didToggleFoul:
					guard state.isEditable else { return .send(.delegate(.didProvokeLock)) }
					state.didFoul.toggle()
					return .send(.delegate(.didEditRoll))

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
	}
}

// MARK: - View

@ViewAction(for: RollEditor.self)
public struct RollEditorView: View {
	public let store: StoreOf<RollEditor>

	private static let selectedStrokeStyle = StrokeStyle(lineWidth: 4, lineCap: .round, dash: [8])

	public var body: some View {
		HStack(alignment: .bottom) {
			VStack(alignment: .leading, spacing: .unitSpacing) {
				Text(Strings.Roll.Properties.Ball.title)
					.font(.caption)
					.italic()
					.foregroundColor(.white)

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
						Image(systemSymbol: .chevronRightSquare)
							.resizable()
							.scaledToFit()
							.foregroundColor(.white)
							.frame(width: .smallIcon, height: .smallIcon)
							.padding(.smallSpacing)
					}
				}
			}

			Spacer()

			Button { send(.didToggleFoul) } label: {
				HStack(spacing: .smallSpacing) {
					Text(Strings.Roll.Properties.Foul.title)
						.foregroundColor(store.didFoul ? Asset.Colors.ScoreSheet.Text.OnBackground.foul.swiftUIColor : .white)
					Image(systemSymbol: store.didFoul ? .fCursiveCircleFill : .fCursiveCircle)
						.resizable()
						.frame(width: .smallIcon, height: .smallIcon)
						.foregroundColor(store.didFoul ? Asset.Colors.ScoreSheet.Text.OnBackground.foul.swiftUIColor : .white)
				}
			}
			.buttonStyle(TappableElement())
		}
		.task { await send(.didStartTask).finish() }
	}
}

#if DEBUG
struct RollEditorPreview: PreviewProvider {
	static var previews: some View {
		RollEditorView(store: .init(
			initialState: .init(
				ballRolled: .init(
					id: UUID(0),
					name: "Yellow",
					kind: .bowlingBall,
					ownerName: nil,
					avatar: .init(id: UUID(0), value: .text("", .default))
				),
				didFoul: false
			),
			reducer: RollEditor.init
		) {
			$0[GearRepository.self].mostRecentlyUsed = { @Sendable _, _ in
				let (stream, continuation) = AsyncThrowingStream<[Gear.Summary], Error>.makeStream()
				let task = Task {
					while !Task.isCancelled {
						try await Task.sleep(for: .seconds(1))
						continuation.yield([
							.init(
								id: UUID(0),
								name: "Yellow",
								kind: .bowlingBall,
								ownerName: "Joseph",
								avatar: .init(id: UUID(0), value: .text("", .default))
							),
							.init(
								id: UUID(1),
								name: "Blue",
								kind: .bowlingBall,
								ownerName: "Sarah",
								avatar: .init(id: UUID(1), value: .text("", .default))
							),
						])
					}
				}
				continuation.onTermination = { _ in task.cancel() }
				return stream
			}
			$0[FeatureFlagsService.self].isEnabled = { @Sendable _ in true }
		})
		.background(.black)
	}
}
#endif

extension Color {
	var rgb: Avatar.Background.RGB {
		let (red, green, blue, _) = UIColor(self).rgba
		return .init(red, green, blue)
	}
}
