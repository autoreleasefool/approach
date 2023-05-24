import ConstantsLibrary
import SwiftUI

public struct AcknowledgementDetailsView: View {
	let acknowledgement: Acknowledgement

	public var body: some View {
		ScrollView {
			Text(acknowledgement.licenseContents)
				.padding(.horizontal)
				.navigationTitle(acknowledgement.name)
		}
	}
}
