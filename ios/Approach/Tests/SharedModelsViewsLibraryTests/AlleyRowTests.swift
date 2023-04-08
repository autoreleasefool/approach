import SharedModelsMocksLibrary
@testable import SharedModelsViewsLibrary
import SnapshotTesting
import SwiftUI
import XCTest

final class AlleyRowTests: XCTestCase {
	func testAlleyRowUnknownPropertiesSnapshot() {
		let rows = List {
			Section {
				AlleyRow(alley: .mock(id: UUID()))
				AlleyRow(alley: .mock(
					id: UUID(),
					address: "123 Test Street",
					material: .synthetic,
					pinFall: .strings,
					mechanism: .dedicated,
					pinBase: .white
				))
			}
		}

		let vc = UIHostingController(rootView: rows)
		vc.view.frame = UIScreen.main.bounds

		assertSnapshot(matching: vc, as: .image(on: .iPhone13ProMax))
	}
}
