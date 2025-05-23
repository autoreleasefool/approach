import SnapshotTesting
import SwiftUI
import Testing
import TestUtilitiesLibrary
@testable import ViewsLibrary

@Suite("PrimaryButton", .tags(.library), .snapshots(record: .failed))
struct PrimaryButtonTests {

	@Test("Primary button snapshots", .tags(.snapshot))
	@MainActor
	func snapshotPrimaryButton() {
		let button = Button { } label: {
			Text("Button")
		}
			.modifier(PrimaryButton())
			.frame(width: 428)

		assertSnapshot(of: button, as: .image)
	}
}
