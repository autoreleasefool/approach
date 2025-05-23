import Foundation
import SnapshotTesting
import Testing
import TestUtilitiesLibrary
@testable import ToastLibrary

@Suite("ToastView Tests", .tags(.library), .snapshots(record: .failed))
struct ToastViewTests {

	@Test("Toast view", .tags(.snapshot))
	@MainActor
	func snapshotToastView() {
		let toastView = ToastView(
			title: "Toast Title",
			action: nil,
			style: .primary,
			onAction: { },
			onDismiss: { }
		)
			.frame(width: 428)

		assertSnapshot(of: toastView, as: .image)
	}

	@Test("Toast view with action", .tags(.snapshot))
	@MainActor
	func snapshotWithAction() {
		let toastView = ToastView(
			title: "Toast Title",
			action: "Action",
			style: .primary,
			onAction: { },
			onDismiss: { }
		)
			.frame(width: 428)

		assertSnapshot(of: toastView, as: .image)
	}

	@Test("Toast view with error style", .tags(.snapshot))
	@MainActor
	func snapshotErrorStyle() {
		let toastView = ToastView(
			title: "Toast Title",
			action: nil,
			style: .error,
			onAction: { },
			onDismiss: { }
		)
			.frame(width: 428)

		assertSnapshot(of: toastView, as: .image)
	}
}
