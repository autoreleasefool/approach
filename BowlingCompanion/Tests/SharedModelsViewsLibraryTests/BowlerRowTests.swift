import SharedModelsMocksLibrary
import SnapshotTesting
import SwiftUI
import XCTest
@testable import SharedModelsViewsLibrary

final class BowlerRowTests: XCTestCase {
	func testBowlerRowSnapshot() {
		let rows = List {
			Section {
				BowlerRow(bowler: .mock(id: UUID()))
				BowlerRow(bowler: .mock(id: UUID(), name: "Sarah"))
			}
		}

		let vc = UIHostingController(rootView: rows)
		vc.view.frame = UIScreen.main.bounds

		assertSnapshot(matching: vc, as: .image(on: .iPhone13ProMax))
	}
}
