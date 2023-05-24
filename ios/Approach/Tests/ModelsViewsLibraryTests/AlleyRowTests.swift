@testable import ModelsViewsLibrary
import SnapshotTesting
import SwiftUI
import XCTest

final class AlleyRowTests: XCTestCase {
	func testAlleyRowUnknownPropertiesSnapshot() {
		let rows = List {
			Section {
				AlleyRow(alley: .init(
					id: UUID(),
					name: "Skyview Lanes",
					address: "123 Fake Street",
					material: nil,
					pinFall: nil,
					mechanism: nil,
					pinBase: nil
				))
				AlleyRow(alley: .init(
					id: UUID(),
					name: "Skyview Lanes",
					address: "123 Fake Street",
					material: .synthetic,
					pinFall: .freefall,
					mechanism: .dedicated,
					pinBase: .black
				))
			}
		}

		let vc = UIHostingController(rootView: rows)
		vc.view.frame = UIScreen.main.bounds

		assertSnapshot(matching: vc, as: .image(on: .iPhone13ProMax))
	}
}
