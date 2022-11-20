import ConstantsLibrary
import SwiftUI

struct AcknowledgementsView: View {
	var body: some View {
		List {
			ForEach(Acknowledgement.all, id: \.name) { acknowledgement in
				NavigationLink(acknowledgement.name, destination: AcknowledgementDetailsView(acknowledgement: acknowledgement))
			}
		}
		.navigationTitle("Acknowledgements")
	}
}
