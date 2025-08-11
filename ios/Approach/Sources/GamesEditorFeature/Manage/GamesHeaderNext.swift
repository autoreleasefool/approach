import AnalyticsServiceInterface
import AssetsLibrary
import ComposableArchitecture
import FeatureActionLibrary
import ModelsLibrary
import PreferenceServiceInterface
import StringsLibrary
import SwiftUI
import ViewsLibrary

@Reducer
public struct GamesHeaderNext: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		@Shared(.bowlerGameIds) public var bowlerGameIds: [Bowler.ID: [Game.ID]]
		@Shared(.currentBowlerId) public var currentBowlerId: Bowler.ID
		@Shared(.currentGameId) public var currentGameId: Game.ID

		public var shimmerColor: Color?
		public var isFlashEditorChangesEnabled: Bool

		var currentGameIndex: Int {
			bowlerGameIds[currentBowlerId]?.firstIndex(of: currentGameId) ?? 0
		}

		init() {
			@Dependency(\.preferences) var preferences
			self.isFlashEditorChangesEnabled = preferences.bool(forKey: .gameShouldNotifyEditorChanges) ?? true
		}
	}

	public enum Action: FeatureAction, ViewAction {
		@CasePathable
		public enum View {
			case task
			case didTapCloseButton
			case didTapSettingsButton
			case didTapShareButton
			case didStartShimmering
		}
		@CasePathable
		public enum Delegate {
			case didCloseEditor
			case didOpenSettings
			case didShareGame
		}
		@CasePathable
		public enum Internal {
			case setShimmerColor(Color?)
			case setFlashEditorChangesEnabled(Bool)
		}

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
	}

	enum CancelID: Sendable { case shimmering }

	@Dependency(\.continuousClock) var clock
	@Dependency(\.preferences) var preferences

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .task:
					return .run { send in
						for await key in preferences.observe(keys: [.gameShouldNotifyEditorChanges]) {
							await send(.internal(.setFlashEditorChangesEnabled(preferences.bool(forKey: key) ?? true)))
						}
					}

				case .didTapCloseButton:
					return .send(.delegate(.didCloseEditor))

				case .didTapSettingsButton:
					return .send(.delegate(.didOpenSettings))

				case .didTapShareButton:
					return .send(.delegate(.didShareGame))

				case .didStartShimmering:
					guard state.isFlashEditorChangesEnabled else { return .none }

					return .run { send in
						for color in [
							Asset.Colors.Primary.light.swiftUIColor.opacity(0.6),
							Asset.Colors.Primary.light.swiftUIColor.opacity(0),
							Asset.Colors.Primary.light.swiftUIColor.opacity(0.6),
							Asset.Colors.Primary.light.swiftUIColor.opacity(0),
							nil,
						] {
							guard !Task.isCancelled else { return }
							await send(.internal(.setShimmerColor(color)), animation: .easeInOut(duration: 0.5))
							try await clock.sleep(for: .milliseconds(500))
						}
					}
					.cancellable(id: CancelID.shimmering, cancelInFlight: true)
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

		AnalyticsReducer<State, Action> { _, action in
			switch action {
			case .view(.didTapShareButton): return Analytics.Game.Shared()
			default: return nil
			}
		}
	}
}

// MARK: - View

@ViewAction(for: GamesHeaderNext.self)
public struct GamesHeaderNextView: View {
	public let store: StoreOf<GamesHeaderNext>

	public var body: some View {
		ZStack {
			HStack {
				Text(Strings.Game.titleWithOrdinal(store.currentGameIndex + 1))
					.font(.caption)
					.foregroundStyle(.white)
					.padding(.tinySpacing)
					.background(
						RoundedRectangle(cornerRadius: .smallRadius)
							.fill(store.shimmerColor ?? Asset.Colors.Primary.default.swiftUIColor.opacity(0))
					)
			}

			HStack {
				headerButton(systemImage: "chevron.backward") { send(.didTapCloseButton) }

				Spacer()

				headerButton(systemImage: "square.and.arrow.up") { send(.didTapShareButton) }

				headerButton(systemImage: "gear") { send(.didTapSettingsButton) }
			}
		}
		.onChange(of: store.currentGameIndex) { send(.didStartShimmering) }
		.task { await send(.task).finish() }
	}

	private func headerButton(systemImage: String, action: @escaping () -> Void) -> some View {
		Button(action: action) {
			Image(systemName: systemImage)
				.resizable()
				.scaledToFit()
				.frame(width: .smallIcon, height: .smallIcon)
				.foregroundStyle(.white)
				.padding()
		}
	}
}
