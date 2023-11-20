@testable import AppInfoService
@testable import AppInfoServiceInterface
import Dependencies
import XCTest

final class AppInfoServiceTests: XCTestCase {
	@Dependency(\.appInfo) var appInfo

	func testRecordNewSession_IncrementsSessionCount() async {
		let sessionCount = LockIsolated(99)

		await withDependencies {
			$0.preferences.contains = { _ in true }
			$0.preferences.getInt = { _ in sessionCount.value }
			$0.preferences.setInt = { _, newValue in sessionCount.setValue(newValue) }
			$0.appInfo.recordNewSession = AppInfoService.liveValue.recordNewSession
		} operation: {
			await appInfo.recordNewSession()
			XCTAssertEqual(sessionCount.value, 100)
		}
	}

	func testNumberOfSessions_DefaultsToZero() {
		withDependencies {
			$0.preferences.contains = { _ in false }
			$0.preferences.getInt = { _ in nil }
			$0.appInfo.numberOfSessions = AppInfoService.liveValue.numberOfSessions
		} operation: {
			XCTAssertEqual(appInfo.numberOfSessions(), 0)
		}
	}

	func testNumberOfSessions_IsIncremented() async {
		let sessionCount = LockIsolated(99)

		await withDependencies {
			$0.preferences.contains = { _ in true }
			$0.preferences.getInt = { _ in sessionCount.value }
			$0.preferences.setInt = { _, newValue in sessionCount.setValue(newValue) }
			$0.appInfo.recordNewSession = AppInfoService.liveValue.recordNewSession
			$0.appInfo.numberOfSessions = AppInfoService.liveValue.numberOfSessions
		} operation: {
			XCTAssertEqual(appInfo.numberOfSessions(), 99)
			await appInfo.recordNewSession()
			XCTAssertEqual(appInfo.numberOfSessions(), 100)
		}
	}

	func testRecordInstallDate_UpdatesInstallDate() async {
		let installDate = LockIsolated<Double?>(nil)

		await withDependencies {
			$0.date.now = Date(timeIntervalSince1970: 123.0)
			$0.preferences.contains = { _ in true }
			$0.preferences.getDouble = { _ in installDate.value }
			$0.preferences.setDouble = { _, newValue in installDate.setValue(newValue) }
			
			$0.appInfo.recordInstallDate = AppInfoService.liveValue.recordInstallDate
		} operation: {
			await appInfo.recordInstallDate()
			XCTAssertEqual(installDate.value, 123.0)
		}
	}

	func testRecordInstallDate_DoesNotUpdateAgain() async {
		let installDate = LockIsolated<Double?>(123.0)

		await withDependencies {
			$0.date.now = Date(timeIntervalSince1970: 456.0)
			$0.preferences.contains = { _ in true }
			$0.preferences.getDouble = { _ in installDate.value }
			$0.preferences.setDouble = { _, newValue in installDate.setValue(newValue) }
			$0.appInfo.recordInstallDate = AppInfoService.liveValue.recordInstallDate
		} operation: {
			await appInfo.recordInstallDate()
			XCTAssertEqual(installDate.value, 123.0)
		}
	}

	func testInstallDate_DefaultsToZero() {
		withDependencies {
			$0.preferences.contains = { _ in false }
			$0.preferences.getDouble = { _ in nil }
			$0.appInfo.installDate = AppInfoService.liveValue.installDate
		} operation: {
			XCTAssertEqual(appInfo.installDate(), Date(timeIntervalSince1970: 0))
		}
	}

	func testInstallDate_IsUpdated() async {
		let installDate = LockIsolated<Double?>(nil)

		await withDependencies {
			$0.date.now = Date(timeIntervalSince1970: 123.0)
			$0.preferences.contains = { _ in true }
			$0.preferences.getDouble = { _ in installDate.value }
			$0.preferences.setDouble = { _, newValue in installDate.setValue(newValue) }
			$0.appInfo.recordInstallDate = AppInfoService.liveValue.recordInstallDate
			$0.appInfo.installDate = AppInfoService.liveValue.installDate
		} operation: {
			XCTAssertEqual(appInfo.installDate(), Date(timeIntervalSince1970: 0))
			await appInfo.recordInstallDate()
			XCTAssertEqual(appInfo.installDate(), Date(timeIntervalSince1970: 123.0))
		}
	}
}
