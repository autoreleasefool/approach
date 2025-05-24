@testable import ModelsLibrary
@testable import ModelsViewsLibrary
import SnapshotTesting
import SwiftUI
import Testing
import TestUtilitiesLibrary

@Suite("AlleyView", .tags(.library), .snapshots(record: .failed))
struct AlleyViewTests {

	@Test("Alley view snapshots", .tags(.snapshot))
	@MainActor
	func snapshotAlleyViews() {
		let rows = VStack {
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
		.frame(width: 240)

		assertSnapshot(of: rows, as: .image)
	}
}
