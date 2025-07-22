import ConstantsLibrary
import StringsLibrary
import SwiftUI

public struct AcknowledgementsView: View {
	public var body: some View {
		List {
			ForEach(Acknowledgement.all, id: \.name) { acknowledgement in
				NavigationLink(acknowledgement.name, destination: AcknowledgementDetailsView(acknowledgement: acknowledgement))
			}
		}
		.navigationTitle(Strings.Settings.Acknowledgements.title)
	}
}
