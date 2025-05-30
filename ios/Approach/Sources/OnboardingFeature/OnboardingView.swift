import AssetsLibrary
import ComposableArchitecture
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsPackageLibrary
import ViewsLibrary

@ViewAction(for: Onboarding.self)
public struct OnboardingView: View {
	@Bindable public var store: StoreOf<Onboarding>

	@Environment(\.safeAreaInsetsProvider) private var safeAreaInsetsProvider
	@State private var minimumSheetSize: CGSize = .zero

	public init(store: StoreOf<Onboarding>) {
		self.store = store
	}

	public var body: some View {
		ZStack {
			OnboardingBackground()
				.opacity(store.step.isShowingHeader ? 0.2 : 0)

			VStack {
				Spacer()

				CenteredScrollView {
					VStack(spacing: 0) {
						Header()
							.padding(.top, .extraLargeSpacing)
							.padding(.horizontal, .standardSpacing)
							.background(OnboardingContainer(fadedEdges: [.top]))
							.opacity(store.step.isShowingHeader ? 1 : 0)

						Description()
							.padding(.horizontal, .standardSpacing)
							.background(OnboardingContainer())
							.opacity(store.step.isShowingMessage ? 1 : 0)

						LovinglyCraftedMessage()
							.padding(.bottom, .extraLargeSpacing)
							.padding(.horizontal, .standardSpacing)
							.background(OnboardingContainer(fadedEdges: [.bottom]))
							.opacity(store.step.isShowingCrafted ? 1 : 0)
					}
				}

				Spacer()

				Button { send(.didTapGetStarted) } label: {
					Text(Strings.Onboarding.getStarted)
						.font(.headline)
						.fontWeight(.heavy)
						.foregroundStyle(Asset.Colors.Action.default)
						.textCase(.uppercase)
						.frame(maxWidth: .infinity)
				}
				.padding(.top, .extraLargeSpacing)
				.padding(.bottom, safeAreaInsetsProvider.get().bottom + .standardSpacing)
				.background(OnboardingContainer(fadedEdges: [.top]))
				.opacity(store.step.isShowingGetStarted ? 1 : 0)
			}
			.ignoresSafeArea(edges: .bottom)
		}

		.sheet(isPresented: $store.isShowingSheet) {
			Logbook(bowlerName: $store.bowlerName) {
				send(.didTapAddBowler)
			}
			.padding(.horizontal)
			.measure(key: MinimumSheetContentSizeKey.self, to: $minimumSheetSize)
			.presentationDragIndicator(.hidden)
			.interactiveDismissDisabled()
			.presentationBackgroundInteraction(.enabled(upThrough: .height(minimumSheetSize.height)))
			.presentationDetents([.height(minimumSheetSize.height == 0 ? 50 : minimumSheetSize.height)])
		}
		.onFirstAppear { send(.didFirstAppear) }
		.toolbar(.hidden, for: .navigationBar)
		.onAppear { send(.onAppear) }
	}
}

private struct MinimumSheetContentSizeKey: PreferenceKey, CGSizePreferenceKey {}
