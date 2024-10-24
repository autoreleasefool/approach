import AssetsLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

struct ImportProgressBanner: View {
	let progress: Import.Progress
	let onDidTapSendEmail: () -> Void
	let onDidTapCopyEmail: () -> Void

	var body: some View {
		switch progress {
		case .notStarted, .pickingFile:
			EmptyView()
		case .importing, .restoring:
			BannerSection(.message(Strings.Import.Importing.inProgress), style: .plain)
		case let .failed(error):
			ErrorBanner(
				message: error.localizedDescription,
				onDidTapSendEmail: onDidTapSendEmail,
				onDidTapCopyEmail: onDidTapCopyEmail
			)
		case .importComplete(.unrecognized):
			ErrorBanner(
				message: Strings.Import.Importing.unrecognized,
				onDidTapSendEmail: onDidTapSendEmail,
				onDidTapCopyEmail: onDidTapCopyEmail
			)
		case .importComplete(.databaseTooNew):
			ErrorBanner(
				message: Strings.Import.Importing.databaseTooNew,
				onDidTapSendEmail: onDidTapSendEmail,
				onDidTapCopyEmail: onDidTapCopyEmail
			)
		case .importComplete(.databaseTooOld):
			ErrorBanner(
				message: Strings.Import.Importing.databaseTooOld,
				onDidTapSendEmail: onDidTapSendEmail,
				onDidTapCopyEmail: onDidTapCopyEmail
			)
		case .importComplete(.success):
			BannerSection(.message(Strings.Import.Importing.successImporting), style: .success)
		case .restoreComplete:
			BannerSection(.message(Strings.Import.Importing.successRestoring), style: .success)
		}
	}
}

private struct ErrorBanner: View {
	let message: String
	let onDidTapSendEmail: () -> Void
	let onDidTapCopyEmail: () -> Void

	var body: some View {
		BannerSection(.titleAndMessage(Strings.Import.Importing.error, message), style: .error)

		Section {
			Group {
				Text(Strings.Import.Importing.report) +
				Text(Strings.supportEmail)
					.foregroundStyle(Asset.Colors.Action.default.swiftUIColor)
			}
			.frame(maxWidth: .infinity, alignment: .leading)
			.onTapGesture(perform: onDidTapCopyEmail)

			Button(action: onDidTapSendEmail) {
				Text(Strings.Import.Importing.Report.sendEmail)
					.frame(maxWidth: .infinity, alignment: .leading)
			}
		}
	}
}
