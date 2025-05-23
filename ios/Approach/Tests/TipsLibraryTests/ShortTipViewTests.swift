import Foundation
import SnapshotTesting
import Testing
import TestUtilitiesLibrary
@testable import TipsLibrary

@Suite("ShortTipView Tests", .tags(.library), .snapshots(record: .failed))
struct ShortTipViewTests {

	@Test("ShortTipView snapshot", .tags(.snapshot))
	@MainActor
	func snapshotShortTipView() {
		let tip = ShortTipView(
			tip: Tip(title: "Tip Title"),
			onDismiss: { }
		)
			.frame(width: 428)

		assertSnapshot(of: tip, as: .image)
	}

	@Test("ShortTipView with message", .tags(.snapshot))
	@MainActor
	func snapshotWithMessage() {
		let tip = ShortTipView(
			tip: Tip(
				title: "Tip Title",
				message: "Here is a long message that describes the tip in detail. It can be multiple lines and should be formatted correctly."
			),
			onDismiss: { }
		)
			.frame(width: 428)

		assertSnapshot(of: tip, as: .image)
	}
}
