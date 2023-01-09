import Foundation
import FoundationExtensionsLibrary
import StringsLibrary
import SwiftUI

public struct VersionView: View {
	public var body: some View {
		Section {
			LabeledContent(
				Strings.Settings.AppInfo.version,
				value: Strings.Settings.AppInfo.appVersion(Bundle.main.appVersionLong, Bundle.main.appBuild)
			)
		} header: {
			Text(Strings.Settings.AppInfo.title)
		} footer: {
			Text(Strings.Settings.AppInfo.copyright)
				.font(.caption)
		}
	}
}
