import StringsLibrary
import SwiftUI
import ViewsLibrary

struct Logbook: View {
	@Binding var bowlerName: String
	let didTapAddBowler: () -> Void

	var body: some View {
		VStack {
			Text(Strings.Onboarding.Logbook.belongsTo)
				.font(.subheadline)
				.opacity(0.7)
				.padding(.top)
				.padding(.horizontal)

			TextField(
				Strings.Onboarding.Logbook.name,
				text: $bowlerName
			)
			.textContentType(.name)
			.multilineTextAlignment(.center)
			.fontWeight(.heavy)

			Rectangle()
				.fill(Color.black)
				.frame(height: 1)
				.frame(maxWidth: .infinity)

			Button(action: didTapAddBowler) {
				Text(Strings.Onboarding.Logbook.addBowler)
					.frame(maxWidth: .infinity)
			}
			.modifier(PrimaryButton())
			.padding(.vertical)
		}
	}
}
