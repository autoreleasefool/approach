import AssetsLibrary
import ComposableArchitecture
import FeatureActionLibrary
import GamesRepositoryInterface
import ModelsLibrary
import PreferenceServiceInterface
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct GameDetailsHeader: Reducer {
	public struct State: Equatable {
		public var currentBowlerName: String
		public var currentLeagueName: String
		public var shimmerColor: Color?
		public var next: NextElement?

		public var isFlashEditorChangesEnabled: Bool

		init(currentBowlerName: String = "", currentLeagueName: String = "", nextElement: NextElement? = nil) {
			self.currentBowlerName = currentBowlerName
			self.currentLeagueName = currentLeagueName
			self.next = nextElement

			@Dependency(\.preferences) var preferences
			self.isFlashEditorChangesEnabled = preferences.bool(forKey: .gameShouldNotifyEditorChanges) ?? true
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didFirstAppear
			case didTapNext(State.NextElement)
		}
		public enum InternalAction: Equatable {
			case setShimmerColor(Color?)
			case setFlashEditorChangesEnabled(Bool)
		}
		public enum DelegateAction: Equatable {
			case didProceed(to: State.NextElement)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	enum CancelID { case shimmering }

	@Dependency(\.continuousClock) var clock
	@Dependency(\.preferences) var preferences

	public var body: some ReducerOf<Self> {
		Reduce { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didFirstAppear:
					return .run { send in
						for await key in preferences.observe(keys: [.gameShouldNotifyEditorChanges]) {
							await send(.internal(.setFlashEditorChangesEnabled(preferences.bool(forKey: key) ?? true)))
						}
					}

				case let .didTapNext(next):
					let shimmeringEffect: Effect<Action>
					switch next {
					case .bowler:
						shimmeringEffect = state.isFlashEditorChangesEnabled ? .none : .run { send in
							for color in [
								Asset.Colors.Primary.light.swiftUIColor,
								Asset.Colors.Primary.light.swiftUIColor.opacity(0),
								Asset.Colors.Primary.light.swiftUIColor,
								Asset.Colors.Primary.light.swiftUIColor.opacity(0),
								nil,
							] {
								guard !Task.isCancelled else { return }
								await send(.internal(.setShimmerColor(color)), animation: .easeInOut(duration: 0.5))
								try await clock.sleep(for: .milliseconds(500))
							}
						}
						.cancellable(id: CancelID.shimmering, cancelInFlight: true)
					case .frame, .game, .roll:
						shimmeringEffect = .none
					}

					return .merge([
						shimmeringEffect,
						.send(.delegate(.didProceed(to: next))),
					])
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .setShimmerColor(color):
					state.shimmerColor = color
					return .none

				case let .setFlashEditorChangesEnabled(enabled):
					state.isFlashEditorChangesEnabled = enabled
					return .none
				}

			case .delegate:
				return .none
			}
		}
	}
}

extension GameDetailsHeader.State {
	public enum NextElement: Equatable, CustomStringConvertible {
		case bowler(name: String, id: Bowler.ID)
		case roll(rollIndex: Int)
		case frame(frameIndex: Int)
		case game(gameIndex: Int, bowler: Bowler.ID, game: Game.ID)

		public var description: String {
			switch self {
			case let .bowler(name, _):
				return name
			case let .roll(rollIndex):
				return Strings.Roll.title(rollIndex + 1)
			case let .frame(frameIndex):
				return Strings.Frame.title(frameIndex + 1)
			case let .game(gameIndex, _, _):
				return Strings.Game.titleWithOrdinal(gameIndex + 1)
			}
		}
	}
}

// MARK: - View

public struct GameDetailsHeaderView: View {
	let store: StoreOf<GameDetailsHeader>

	public var body: some View {
		WithViewStore(store, observe: { $0 }, send: { .view($0) }, content: { viewStore in
			HStack(alignment: .top) {
				VStack(alignment: .leading, spacing: .tinySpacing) {
					Text(viewStore.currentBowlerName)
						.font(.headline)
						.padding(.tinySpacing)
						.background(
							RoundedRectangle(cornerRadius: .smallRadius)
								.fill(viewStore.shimmerColor ?? Asset.Colors.Primary.light.swiftUIColor.opacity(0))
						)

					Text(viewStore.currentLeagueName)
						.font(.subheadline)
						.padding(.tinySpacing)
						.background(
							RoundedRectangle(cornerRadius: .smallRadius)
								.fill(viewStore.shimmerColor ?? Asset.Colors.Primary.light.swiftUIColor.opacity(0))
						)
				}

				Spacer()

				if let next = viewStore.next {
					Button { viewStore.send(.didTapNext(next)) } label: {
						HStack {
							Text(String(describing: next))
								.font(.caption)
							Image(systemSymbol: .chevronForward)
								.resizable()
								.scaledToFit()
								.frame(width: .tinyIcon, height: .tinyIcon)
						}
					}
					.contentShape(Rectangle())
					.buttonStyle(TappableElement())
				}
			}
			.task { await viewStore.send(.didFirstAppear).finish() }
		})
	}
}

#if DEBUG
struct GameDetailsHeaderPreview: PreviewProvider {
	static var previews: some View {
		GameDetailsHeaderView(
			store: .init(
				initialState: GameDetailsHeader.State(
					currentBowlerName: "Joseph Roque",
					currentLeagueName: "Majors",
					nextElement: .bowler(name: "Sarah", id: .init(0))
				),
				reducer: GameDetailsHeader.init
			)
		)
	}
}
#endif
