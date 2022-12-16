import SharedModelsLibrary
import SwiftUI

public struct LaneRow: View {
	let lane: Lane

	public init(lane: Lane) {
		self.lane = lane
	}

	public var body: some View {
		HStack(alignment: .center, spacing: .standardSpacing) {
			Text(lane.label)
				.frame(maxWidth: .infinity, alignment: .leading)
			if lane.isAgainstWall {
				Image(systemName: "decrease.quotelevel")
					.opacity(0.7)
			}
		}
	}
}

#if DEBUG
struct LaneRowPreview: PreviewProvider {
	static var previews: some View {
		List {
			Section {
				LaneRow(lane: .init(id: .init(), label: "1", isAgainstWall: true, alley: .init()))
				LaneRow(lane: .init(id: .init(), label: "2", isAgainstWall: false, alley: .init()))
				LaneRow(lane: .init(id: .init(), label: "3", isAgainstWall: false, alley: .init()))
				LaneRow(lane: .init(id: .init(), label: "4", isAgainstWall: true, alley: .init()))
			}
			.listRowBackground(Color(uiColor: .secondarySystemBackground))
		}
		.scrollContentBackground(.hidden)
	}
}
#endif
