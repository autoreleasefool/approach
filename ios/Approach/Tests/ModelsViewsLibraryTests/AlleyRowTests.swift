@testable import ModelsLibrary
@testable import ModelsViewsLibrary
import SnapshotTesting
import SwiftUI
import XCTest

final class AlleyRowTests: XCTestCase {
	func testAlleyRowUnknownPropertiesSnapshot() {
		let rows = List {
			Section {
				Alley.View(.init(
					id: UUID(),
					name: "Skyview Lanes",
					material: nil,
					pinFall: nil,
					mechanism: nil,
					pinBase: nil,
					location: nil
				))
				Alley.View(.init(
					id: UUID(),
					name: "Skyview Lanes",
					material: .synthetic,
					pinFall: .freefall,
					mechanism: .dedicated,
					pinBase: .black,
					location: .init(
						id: UUID(),
						title: "Skyview Lanes",
						subtitle: "123 Fake Street",
						coordinate: .init(latitude: 1.0, longitude: 1.0)
					)
				))
			}
		}

		let vc = UIHostingController(rootView: rows)
		vc.view.frame = UIScreen.main.bounds

		assertSnapshot(matching: vc, as: .image(on: .iPhone13ProMax))
	}
}
