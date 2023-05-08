import SwiftUI

public struct GameSummaryHeader: View {
	let bowlerName: String
	let leagueName: String

	public var body: some View {
		Section {
			VStack(alignment: .leading) {
				Text(bowlerName)
					.font(.headline)
					.frame(maxWidth: .infinity, alignment: .leading)
				Text(leagueName)
					.font(.subheadline)
					.frame(maxWidth: .infinity, alignment: .leading)
			}
			.frame(maxWidth: .infinity)
		}
		.listRowInsets(EdgeInsets())
		.listRowBackground(Color(uiColor: .secondarySystemBackground))
	}
}
