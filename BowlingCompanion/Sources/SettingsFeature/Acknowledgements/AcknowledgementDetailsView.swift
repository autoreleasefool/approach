import ConstantsLibrary
import SwiftUI

struct AcknowledgementDetailsView: View {
	let acknowledgement: Acknowledgement

	var body: some View {
		ScrollView {
			Text(acknowledgement.licenseContents)
				.padding(.horizontal)
				.navigationTitle(acknowledgement.name)
		}
	}
}
