import ConstantsLibrary
import Foundation
import StringsLibrary
import SwiftUI

public struct VersionView: View {
	public var body: some View {
		Section {
			LabeledContent(
				Strings.Settings.AppInfo.version,
				value: AppConstants.appVersionReadable
			)
		} header: {
			Text(Strings.Settings.AppInfo.title)
		} footer: {
			Text(Strings.Settings.AppInfo.copyright)
				.font(.caption)
		}
	}
}
