@testable import ModelsLibrary
@testable import ModelsViewsLibrary
import SnapshotTesting
import SwiftUI
import Testing
import TestUtilitiesLibrary

@Suite("LaneView", .tags(.library), .snapshots(record: .failed))
struct LaneViewTests {

	@Test(
		"Lane view snapshots",
		.tags(.snapshot),
		arguments: Lane.Position.allCases
	)
	@MainActor
	func snapshotLaneView(position: Lane.Position) {
		let view = Lane.View(label: "12", position: position)
			.frame(width: 240)

		assertSnapshot(of: view, as: .image, named: "\(position)")
	}
}

