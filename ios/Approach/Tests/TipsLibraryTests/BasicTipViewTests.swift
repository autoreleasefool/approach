import Foundation
import SnapshotTesting
import Testing
import TestUtilitiesLibrary
@testable import TipsLibrary

@Suite("BasicTipView Tests", .tags(.library), .snapshots(record: .failed))
struct BasicTipViewTests {

	@Test("BasicTipView snapshot", .tags(.snapshot))
	@MainActor
	func snapshotBasicTipView() {
		let tip = BasicTipView(
			tip: Tip(title: "Tip Title"),
			isDismissable: false,
			onDismiss: { }
		)
			.frame(width: 428)

		assertSnapshot(of: tip, as: .image)
	}

	@Test("BasicTipView with message", .tags(.snapshot))
	@MainActor
	func snapshotWithMessage() {
		let tip = BasicTipView(
			tip: Tip(
				title: "Tip Title",
				message: "Here is a long message that describes the tip in detail. It can be multiple lines and should be formatted correctly."
			),
			isDismissable: false,
			onDismiss: { }
		)
			.frame(width: 428)

		assertSnapshot(of: tip, as: .image)
	}

	@Test("BasicTipView with dismiss", .tags(.snapshot))
	@MainActor
	func snapshotWithDismiss() {
		let tip = BasicTipView(
			tip: Tip(title: "Tip Title"),
			isDismissable: true,
			onDismiss: { }
		)
			.frame(width: 428)

		assertSnapshot(of: tip, as: .image)
	}

	@Test("BasicTipView with dismiss and message", .tags(.snapshot))
	@MainActor
	func snapshotWithDismissAndMessage() {
		let tip = BasicTipView(
			tip: Tip(
				title: "Tip Title",
				message: "Here is a long message that describes the tip in detail. It can be multiple lines and should be formatted correctly."
			),
			isDismissable: true,
			onDismiss: { }
		)
			.frame(width: 428)

		assertSnapshot(of: tip, as: .image)
	}
}
