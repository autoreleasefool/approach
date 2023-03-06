import SharedModelsMocksLibrary
import SnapshotTesting
import SwiftUI
import XCTest
@testable import SharedModelsViewsLibrary

final class LaneRowTests: XCTestCase {
	func testLaneRowSnapshot() {
		let rows = List {
			Section {
				LaneRow(lane: .mock(id: UUID(), label: "1", alley: UUID()))
				LaneRow(lane: .mock(id: UUID(), label: "2", isAgainstWall: true, alley: UUID()))
			}
		}

		let vc = UIHostingController(rootView: rows)
		vc.view.frame = UIScreen.main.bounds

		assertSnapshot(matching: vc, as: .image(on: .iPhone13ProMax))
	}
}
