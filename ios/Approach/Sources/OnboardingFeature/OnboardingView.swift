import AssetsLibrary
import ComposableArchitecture
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import ViewsLibrary

public struct OnboardingView: View {
	let store: StoreOf<Onboarding>

	@State private var minimumSheetSize: CGSize = .zero

	public init(store: StoreOf<Onboarding>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: { $0 }, send: { .view($0) }, content: { viewStore in
			VStack(spacing: 0) {
				Group {
					Text(Strings.Onboarding.Header.welcomeTo)
						.font(.title2)
						.fontWeight(.heavy)
						.frame(maxWidth: .infinity, alignment: .leading)
						.padding(.top)
						.padding(.bottom, .tinySpacing)

					Text(Strings.Onboarding.Header.appName)
						.font(.title)
						.fontWeight(.heavy)
						.foregroundColor(Asset.Colors.Primary.default)
						.frame(maxWidth: .infinity, alignment: .leading)
						.padding(.bottom, .standardSpacing)
				}
				.opacity(viewStore.step.isShowingHeader ? 1 : 0)

				Text(Strings.Onboarding.Message.description)
					.font(.body)
					.fontWeight(.medium)
					.frame(maxWidth: .infinity, alignment: .leading)
					.padding(.bottom, .largeSpacing)
					.opacity(viewStore.step.isShowingMessage ? 1 : 0)

				Text(Strings.Onboarding.Message.lovinglyCrafted)
					.font(.caption)
					.fontWeight(.bold)
					.frame(maxWidth: .infinity, alignment: .leading)
					.opacity(viewStore.step.isShowingCrafted ? 1 : 0)

				Spacer()

				Button {
					viewStore.send(.didTapGetStarted)
				} label: {
					Text(Strings.Onboarding.getStarted)
						.font(.headline)
						.fontWeight(.heavy)
						.foregroundColor(Asset.Colors.Action.default)
						.textCase(.uppercase)
				}
				.padding(.bottom)
				.opacity(viewStore.step.isShowingGetStarted ? 1 : 0)
			}
			.padding(.horizontal)
			.sheet(isPresented: viewStore.$isShowingSheet) {
				VStack {
					Text(Strings.Onboarding.Logbook.belongsTo)
						.font(.subheadline)
						.opacity(0.7)
						.padding(.top)
						.padding(.horizontal)

					TextField(
						Strings.Onboarding.Logbook.name,
						text: viewStore.$bowlerName
					)
					.textContentType(.name)
					.multilineTextAlignment(.center)
					.fontWeight(.heavy)

					Rectangle()
						.fill(Color.black)
						.frame(height: 1)
						.frame(maxWidth: .infinity)

					Button {
						viewStore.send(.didTapAddBowler)
					} label: {
						Text(Strings.Onboarding.Logbook.addBowler)
							.frame(maxWidth: .infinity)
					}
					.modifier(PrimaryButton())
					.padding(.vertical)
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
		})
	}
}

private struct MinimumSheetContentSizeKey: PreferenceKey, CGSizePreferenceKey {}
