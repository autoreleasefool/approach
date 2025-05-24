@testable import ModelsLibrary
@testable import ModelsViewsLibrary
import SnapshotTesting
import SwiftUI
import Testing
import TestUtilitiesLibrary

@Suite("BowlerView", .tags(.library), .snapshots(record: .failed))
struct BowlerViewTests {

	@Test(
		"Bowler view snapshots",
		.tags(.snapshot),
		arguments: [Bowler.Kind.opponent, Bowler.Kind.playable, nil]
	)
	@MainActor
	func snapshotBowlerView(kind: Bowler.Kind?) {
		let view = Bowler.View(name: "Joseph", kind: kind)
			.frame(width: 240)

		assertSnapshot(of: view, as: .image, named: "\(kind?.rawValue ?? "nil")")
	}
}
