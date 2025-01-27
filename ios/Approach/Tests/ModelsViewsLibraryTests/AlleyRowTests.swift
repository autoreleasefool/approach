@testable import ModelsLibrary
@testable import ModelsViewsLibrary
import SnapshotTesting
import SwiftUI
import Testing

@Suite(.snapshots(record: .missing))
struct AlleyRowTests {
	@Test("Alley row snapshots", .tags(.snapshot))
	@MainActor
	func snapshotAlleyRows() {
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
		.frame(width: 428)

		assertSnapshot(of: rows, as: .image)
	}
}
