@testable import ModelsLibrary
@testable import ModelsViewsLibrary
import SnapshotTesting
import SwiftUI
import Testing
import TestUtilitiesLibrary

@Suite("GearView", .tags(.library), .snapshots(record: .failed))
struct GearViewTests {

	@Test(
		"Gear view snapshots",
		.tags(.snapshot),
		arguments: Gear.Kind.allCases
	)
	@MainActor
	func snapshotGearView(kind: Gear.Kind) {
		let view = Gear.View(
			name: "Bowling Ball",
			kind: kind,
			ownerName: nil,
			avatar: .text("B", .default)
		)
			.frame(width: 240)

		assertSnapshot(of: view, as: .image, named: "\(kind)")
	}

	@Test("Gear view with owner", .tags(.snapshot))
	@MainActor
	func snapshotWithOwner() {
		let view = Gear.View(
			name: "Bowling Ball",
			kind: .bowlingBall,
			ownerName: "Joseph",
			avatar: .text("J", .default)
		)
			.frame(width: 240)

		assertSnapshot(of: view, as: .image)
	}
}
