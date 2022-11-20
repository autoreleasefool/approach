import ConstantsLibrary
import SwiftUI

struct AcknowledgementDetailsView: View {
	let acknowledgement: Acknowledgement

	var body: some View {
		Text(acknowledgement.licenseContents)
			.navigationTitle(acknowledgement.name)
	}
}
