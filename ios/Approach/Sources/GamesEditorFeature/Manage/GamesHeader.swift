import AssetsLibrary
import ComposableArchitecture
import FeatureActionLibrary
import FeatureFlagsServiceInterface
import ModelsLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct GamesHeader: Reducer {
	public struct State: Equatable {
		public var currentGameIndex: Int = 0
		public let isSharingGameEnabled: Bool
		public var shimmerColor: Color?

		init() {
			@Dependency(\.featureFlags) var featureFlags
			self.isSharingGameEnabled = featureFlags.isEnabled(.sharingGame)
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didTapCloseButton
			case didTapSettingsButton
			case didTapShareButton
			case didStartShimmering
		}
		public enum DelegateAction: Equatable {
			case didCloseEditor
			case didOpenSettings
			case didShareGame
		}
		public enum InternalAction: Equatable {
			case setShimmerColor(Color?)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	enum CancelID { case shimmering }

	@Dependency(\.continuousClock) var clock

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didTapCloseButton:
					return .send(.delegate(.didCloseEditor))

				case .didTapSettingsButton:
					return .send(.delegate(.didOpenSettings))

				case .didTapShareButton:
					return .send(.delegate(.didShareGame))

				case .didStartShimmering:
					return .run { send in
						for color in [
							Asset.Colors.Primary.default.swiftUIColor.opacity(0.6),
							Asset.Colors.Primary.default.swiftUIColor.opacity(0),
							Asset.Colors.Primary.default.swiftUIColor.opacity(0.6),
							Asset.Colors.Primary.default.swiftUIColor.opacity(0),
							nil
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
				}

			case .delegate:
				return .none
			}
		}
	}
}

// MARK: - View

public struct GamesHeaderView: View {
	let store: StoreOf<GamesHeader>

	public var body: some View {
		WithViewStore(store, observe: { $0 }, send: { .view($0) }, content: { viewStore in
			ZStack {
				HStack {
					Text(Strings.Game.titleWithOrdinal(viewStore.currentGameIndex + 1))
						.font(.caption)
						.foregroundColor(.white)
						.padding(.tinySpacing)
						.background(
							RoundedRectangle(cornerRadius: .smallRadius)
								.fill(viewStore.shimmerColor ?? Asset.Colors.Primary.default.swiftUIColor.opacity(0))
						)
				}

				HStack {
					headerButton(systemSymbol: .chevronBackward) { viewStore.send(.didTapCloseButton) }

					Spacer()

					if viewStore.isSharingGameEnabled {
						headerButton(systemSymbol: .squareAndArrowUp) { viewStore.send(.didTapShareButton) }
					}

					headerButton(systemSymbol: .gear) { viewStore.send(.didTapSettingsButton) }
				}
			}
			.onChange(of: viewStore.currentGameIndex) { _ in
				viewStore.send(.didStartShimmering)
			}
		})
	}

	private func headerButton(systemSymbol: SFSymbol, action: @escaping () -> Void) -> some View {
		Button(action: action) {
			Image(systemSymbol: systemSymbol)
				.resizable()
				.scaledToFit()
				.frame(width: .smallIcon, height: .smallIcon)
				.foregroundColor(.white)
				.padding()
		}
	}
}

#if DEBUG
struct GamesHeaderPreview: PreviewProvider {
	static var previews: some View {
		GamesHeaderView(store: .init(
			initialState: GamesHeader.State(),
			reducer: {
				GamesHeader()
					.dependency(\.featureFlags.isEnabled, { _ in true })
			}
		))
		.background(.black)
	}
}
#endif
