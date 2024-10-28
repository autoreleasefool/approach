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
public struct GameDetailsHeader: Reducer, Sendable {
	@ObservableState
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

	public enum Action: FeatureAction, ViewAction {
		@CasePathable
		public enum View {
			case didStartTask
			case didTapNext(State.NextElement)
		}
		@CasePathable
		public enum Internal {
			case startShimmering
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
				case .didStartTask:
					return .run { send in
						for await key in preferences.observe(keys: [.gameShouldNotifyEditorChanges]) {
							await send(.internal(.setFlashEditorChangesEnabled(preferences.bool(forKey: key) ?? true)))
						}
					}

				case let .didTapNext(next):
					let shimmeringEffect: Effect<Action>
					switch next {
					case .bowler:
						shimmeringEffect = state.shouldStartShimmering()
					case .frame, .game, .roll:
						shimmeringEffect = .none
					}

					return .merge(
						shimmeringEffect,
						.send(.delegate(.didProceed(to: next)))
					)
				}

			case let .internal(internalAction):
				switch internalAction {
				case .startShimmering:
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
}

// MARK: - View

@ViewAction(for: GameDetailsHeader.self)
public struct GameDetailsHeaderView: View {
	public let store: StoreOf<GameDetailsHeader>

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

			if let next = store.next {
				Button { send(.didTapNext(next)) } label: {
					HStack {
						Text(String(describing: next))
							.font(.caption)
						Image(systemSymbol: .chevronForward)
							.resizable()
							.scaledToFit()
							.frame(width: .tinyIcon, height: .tinyIcon)
					}
					.foregroundColor(Asset.Colors.Text.onAction)
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
		.task { await send(.didStartTask).finish() }
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
