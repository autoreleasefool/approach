import AssetsLibrary
import ComposableArchitecture
import FeatureActionLibrary
import GamesRepositoryInterface
import ModelsLibrary
import PreferenceServiceInterface
import StringsLibrary
import SwiftUI
import ViewsLibrary

@Reducer
public struct GameDetailsHeaderNext: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		@Shared(.currentBowlerId) public var currentBowlerId: Bowler.ID
		@Shared(.game) public var game: Game.Edit?
		@Shared(.nextHeaderElement) public var nextHeaderElement: NextElement?

		public var isFlashEditorChangesEnabled: Bool
		public var shimmerColor: Color?

		var currentBowlerName: String {
			game?.bowler.name ?? ""
		}

		var currentLeagueName: String {
			game?.league.name ?? ""
		}

		init() {
			@Dependency(\.preferences) var preferences
			self.isFlashEditorChangesEnabled = preferences.bool(forKey: .gameShouldNotifyEditorChanges) ?? true
		}

		public enum NextElement: Equatable, Sendable, CustomStringConvertible {
			case bowler(name: String, id: Bowler.ID)
			case roll(rollIndex: Int)
			case frame(frameIndex: Int)
			case game(gameIndex: Int, bowler: Bowler.ID, game: Game.ID)

			public var description: String {
				switch self {
				case let .bowler(name, _):
					name
				case let .roll(rollIndex):
					Strings.Roll.title(rollIndex + 1)
				case let .frame(frameIndex):
					Strings.Frame.title(frameIndex + 1)
				case let .game(gameIndex, _, _):
					Strings.Game.titleWithOrdinal(gameIndex + 1)
				}
			}
		}
	}

	public enum Action: FeatureAction, ViewAction {
		@CasePathable
		public enum View {
			case task
			case onAppear
			case didTapNext(State.NextElement)
		}
		@CasePathable
		public enum Internal {
			case bowlerIdDidChange(Bowler.ID)
			case setShimmerColor(Color?)
			case setFlashEditorChangesEnabled(Bool)
		}
		@CasePathable
		public enum Delegate {
			case didProceed(to: State.NextElement)
		}

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
	}

	enum CancelID: Sendable { case shimmering }

	@Dependency(\.continuousClock) var clock
	@Dependency(\.preferences) var preferences

	public var body: some ReducerOf<Self> {
		Reduce { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return .publisher {
						state.$currentBowlerId.publisher
							.map { .internal(.bowlerIdDidChange($0)) }
					}

				case .task:
					return .run { send in
						for await key in preferences.observe(keys: [.gameShouldNotifyEditorChanges]) {
							await send(.internal(.setFlashEditorChangesEnabled(preferences.bool(forKey: key) ?? true)))
						}
					}

				case let .didTapNext(next):
					return .send(.delegate(.didProceed(to: next)))
				}

			case let .internal(internalAction):
				switch internalAction {
				case .bowlerIdDidChange:
					return startShimmering(ifEnabled: state.isFlashEditorChangesEnabled)

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

	func startShimmering(ifEnabled shimmerEnabled: Bool) -> Effect<Action> {
		shimmerEnabled ? .run { send in
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
		.cancellable(id: CancelID.shimmering, cancelInFlight: true) : .none
	}
}

// MARK: - View

@ViewAction(for: GameDetailsHeaderNext.self)
public struct GameDetailsHeaderNextView: View {
	public let store: StoreOf<GameDetailsHeaderNext>

	public var body: some View {
		HStack(alignment: .center) {
			VStack(alignment: .leading, spacing: .tinySpacing) {
				Text(store.currentBowlerName)
					.font(.headline)
					.padding(.tinySpacing)
					.background(
						RoundedRectangle(cornerRadius: .smallRadius)
							.fill(store.shimmerColor ?? Asset.Colors.Primary.light.swiftUIColor.opacity(0))
					)

				Text(store.currentLeagueName)
					.font(.subheadline)
					.padding(.tinySpacing)
					.background(
						RoundedRectangle(cornerRadius: .smallRadius)
							.fill(store.shimmerColor ?? Asset.Colors.Primary.light.swiftUIColor.opacity(0))
					)
			}

			Spacer()

			if let next = store.nextHeaderElement {
				Button { send(.didTapNext(next)) } label: {
					HStack {
						Text(String(describing: next))
							.font(.caption)
						Image(systemName: "chevron.forward")
							.resizable()
							.scaledToFit()
							.frame(width: .tinyIcon, height: .tinyIcon)
					}
					.foregroundStyle(Asset.Colors.Text.onAction)
					.padding(.standardSpacing)
					.background(
						RoundedRectangle(cornerRadius: .standardRadius)
							.fill(Asset.Colors.Action.default.swiftUIColor)
					)
				}
				.contentShape(Rectangle())
				.buttonStyle(TappableElement())
			}
		}
		.task { await send(.task).finish() }
	}
}
