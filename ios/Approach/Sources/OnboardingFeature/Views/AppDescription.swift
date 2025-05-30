import AssetsLibrary
import StringsLibrary
import SwiftUI

struct Header: View {
	var body: some View {
		VStack(spacing: 0) {
			Text(Strings.Onboarding.Header.welcomeTo)
				.font(.title2)
				.fontWeight(.heavy)
				.frame(maxWidth: .infinity, alignment: .leading)
				.padding(.top)
				.padding(.bottom, .tinySpacing)

			Text(Strings.Onboarding.Header.appName)
				.font(.title)
				.fontWeight(.heavy)
				.foregroundStyle(Asset.Colors.Primary.default)
				.frame(maxWidth: .infinity, alignment: .leading)
				.padding(.bottom, .standardSpacing)
		}
	}
}

struct Description: View {
	var body: some View {
		Text(Strings.Onboarding.Message.description)
			.font(.body)
			.fontWeight(.medium)
			.frame(maxWidth: .infinity, alignment: .leading)
			.padding(.bottom, .largeSpacing)
	}
}

struct LovinglyCraftedMessage: View {
	var body: some View {
		Text(Strings.Onboarding.Message.lovinglyCrafted)
			.font(.caption)
			.fontWeight(.bold)
			.frame(maxWidth: .infinity, alignment: .leading)
	}
}
