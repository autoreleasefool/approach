import StringsLibrary
import SwiftUI
import SwiftUIExtensionsPackageLibrary
import ViewsLibrary

struct DevelopmentSection: View {
	let appName: String
	let appVersion: String
	let onTapViewSourceButton: () -> Void
	let onDeveloperDetailsFirstAppear: () -> Void
	let isShowingBugReportEmail: Binding<Bool>
	let isShowingSendFeedbackEmail: Binding<Bool>

	var body: some View {
		Section {
			NavigationLink(
				Strings.Settings.Help.developer,
				destination: DeveloperDetailsView()
					.onFirstAppear(perform: onDeveloperDetailsFirstAppear)
			)
			Button(Strings.Settings.Help.viewSource, action: onTapViewSourceButton)
			// FIXME: enable tip jar
			//				NavigationLink("Tip Jar", destination: TipJarView())
		} header: {
			Text(Strings.Settings.Help.Development.title)
		} footer: {
			Text(Strings.Settings.Help.Development.help(appName))
		}
		.sheet(isPresented: isShowingBugReportEmail) {
			EmailView(
				content: .init(
					recipients: [Strings.supportEmail],
					subject: Strings.Settings.Help.ReportBug.subject(appVersion)
				)
			)
		}
		.sheet(isPresented: isShowingSendFeedbackEmail) {
			EmailView(
				content: .init(
					recipients: [Strings.supportEmail]
				)
			)
		}
	}
}
