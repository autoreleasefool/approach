import SharedModelsMocksLibrary
import SnapshotTesting
import SwiftUI
import XCTest
@testable import SharedModelsViewsLibrary

final class GearRowTests: XCTestCase {
	func testGearRowSnapshot() {
		let rows = List {
			Section {
				GearRow(gear: .mock(id: UUID()))
				GearRow(gear: .mock(id: UUID(), name: "Towel", kind: .towel))
				GearRow(gear: .mock(id: UUID(), name: "Shoes", kind: .shoes))
			}
		}

		let vc = UIHostingController(rootView: rows)
		vc.view.frame = UIScreen.main.bounds

		assertSnapshot(matching: vc, as: .image(on: .iPhone13ProMax))
	}
}
