import SnapshotTesting
import SwiftUI
@testable import ViewsLibrary
import XCTest

final class EditButtonTests: XCTestCase {
	func testEditButtonSnapshot() {
		let editButton = EditButton { }
		let vc = UIHostingController(rootView: editButton)
		vc.view.frame = UIScreen.main.bounds

		assertSnapshot(matching: vc, as: .image(on: .iPhoneSe))
	}
}
