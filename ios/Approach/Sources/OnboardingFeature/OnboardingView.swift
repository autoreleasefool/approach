import AssetsLibrary
import ComposableArchitecture
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import ViewsLibrary

public struct OnboardingView: View {
	let store: StoreOf<Onboarding>

	@Environment(\.safeAreaInsets) private var safeAreaInsets
	@State private var minimumSheetSize: CGSize = .zero

	public init(store: StoreOf<Onboarding>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: { $0 }, send: { .view($0) }, content: { viewStore in
			ZStack {
				OnboardingBackground()
					.opacity(viewStore.step.isShowingHeader ? 0.2 : 0)

				VStack {
					Spacer()

					CenteredScrollView {
						VStack(spacing: 0) {
							Header()
								.padding(.top, .extraLargeSpacing)
								.padding(.horizontal, .standardSpacing)
								.background(OnboardingContainer(fadedEdges: [.top]))
								.opacity(viewStore.step.isShowingHeader ? 1 : 0)

							Description()
								.padding(.horizontal, .standardSpacing)
								.background(OnboardingContainer())
								.opacity(viewStore.step.isShowingMessage ? 1 : 0)

							LovinglyCraftedMessage()
								.padding(.bottom, .extraLargeSpacing)
								.padding(.horizontal, .standardSpacing)
								.background(OnboardingContainer(fadedEdges: [.bottom]))
								.opacity(viewStore.step.isShowingCrafted ? 1 : 0)
						}
					}

					Spacer()

					Button { viewStore.send(.didTapGetStarted) } label: {
						Text(Strings.Onboarding.getStarted)
							.font(.headline)
							.fontWeight(.heavy)
							.foregroundColor(Asset.Colors.Action.default)
							.textCase(.uppercase)
							.frame(maxWidth: .infinity)
					}
					.padding(.top, .extraLargeSpacing)
					.padding(.bottom, safeAreaInsets.bottom + .standardSpacing)
					.background(OnboardingContainer(fadedEdges: [.top]))
					.opacity(viewStore.step.isShowingGetStarted ? 1 : 0)
				}
				.ignoresSafeArea(edges: .bottom)
			}

			.sheet(isPresented: viewStore.$isShowingSheet) {
				Logbook(bowlerName: viewStore.$bowlerName) {
					viewStore.send(.didTapAddBowler)
				}
				.padding(.horizontal)
				.measure(key: MinimumSheetContentSizeKey.self, to: $minimumSheetSize)
				.presentationDragIndicator(.hidden)
				.interactiveDismissDisabled()
				.presentationBackgroundInteraction(.enabled(upThrough: .height(minimumSheetSize.height)))
				.presentationDetents([.height(minimumSheetSize.height == 0 ? 50 : minimumSheetSize.height)])
			}
			.onFirstAppear { viewStore.send(.didFirstAppear) }
			.toolbar(.hidden, for: .navigationBar)
			.onAppear { viewStore.send(.onAppear) }
		})
	}
}

private struct MinimumSheetContentSizeKey: PreferenceKey, CGSizePreferenceKey {}
