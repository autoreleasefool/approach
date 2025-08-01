import ConstantsLibrary
import SwiftUI

struct AcknowledgementDetailsView: View {
	let acknowledgement: Acknowledgement

	var body: some View {
		ScrollView {
			Text(acknowledgement.licenseText ?? "")
				.padding(.horizontal)
				.navigationTitle(acknowledgement.name)
		}
	}
}
