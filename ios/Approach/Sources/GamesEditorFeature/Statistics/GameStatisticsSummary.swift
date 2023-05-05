import AssetsLibrary
import SwiftUI

public struct GameStatisticsSummary: View {
	public var body: some View {
			Grid(horizontalSpacing: .smallSpacing) {
				GridRow {
					Text("Middle Hit %")
						.foregroundColor(.white)
						.frame(maxWidth: .infinity)
						.gridColumnAlignment(.leading)
					Text("75%")
						.foregroundColor(.white)
						.gridColumnAlignment(.trailing)
//						.gridCellUnsizedAxes(.horizontal)
					Text("+12%")
						.foregroundColor(.appSuccess)
						.gridColumnAlignment(.leading)
//						.gridCellUnsizedAxes(.horizontal)
				}
//				.gridColumnAlignment(.leading)

				GridRow {
					Text("Pins left on deck")
						.frame(maxWidth: .infinity)
						.foregroundColor(.white)
						.gridColumnAlignment(.leading)
					Text("14")
						.foregroundColor(.white)
						.gridColumnAlignment(.trailing)
//						.gridCellUnsizedAxes(.horizontal)
					Text("+8%")
						.foregroundColor(.appDestructive)
						.gridColumnAlignment(.leading)
//						.gridCellUnsizedAxes(.horizontal)
				}

			}
			.gridColumnAlignment(.leading)
//			.gridCellUnsizedAxes(.horizontal)
//			.gridCellUnsizedAxes(.vertical)
	}
}
