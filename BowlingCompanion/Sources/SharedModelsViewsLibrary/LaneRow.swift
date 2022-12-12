import SharedModelsLibrary
import SwiftUI

public struct LaneRow: View {
	let lane: Lane

	public init(lane: Lane) {
		self.lane = lane
	}

	public var body: some View {
		HStack {
			Text(lane.label)
			if lane.isAgainstWall {
				Spacer()
				Text("Wall")
					.font(.caption)
			}
		}
	}
}
