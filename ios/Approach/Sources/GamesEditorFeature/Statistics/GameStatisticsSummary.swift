import AssetsLibrary
import SwiftUI

public struct GameStatisticsSummary: View {
	public var body: some View {
		Grid(horizontalSpacing: .smallSpacing, verticalSpacing: .tinySpacing) {
				GridRow {
					HStack {
						Text("Middle Hit %")
							.foregroundColor(.white)
							.gridColumnAlignment(.leading)
						Spacer()

					}
					Text("75%")
						.foregroundColor(.white)
						.gridColumnAlignment(.trailing)
					Text("+12%")
						.foregroundColor(.appSuccess)
						.gridColumnAlignment(.leading)
				}

				Divider().hidden()

				GridRow {
					HStack {
						Text("Pins left on deck")
							.foregroundColor(.white)
							.gridColumnAlignment(.leading)
						Spacer()
					}
					Text("14")
						.foregroundColor(.white)
						.gridColumnAlignment(.trailing)
					Text("+8%")
						.foregroundColor(.appDestructive)
						.gridColumnAlignment(.leading)
				}
			}
	}
}
