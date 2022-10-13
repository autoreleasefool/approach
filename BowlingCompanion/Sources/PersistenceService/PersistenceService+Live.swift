import Foundation
import PersistenceServiceInterface
import RealmSwift

extension PersistenceService {
	public static func live(queue: DispatchQueue = .init(label: "PersistenceService")) -> Self {
		let live = Live(queue: queue)
		return .init(
			read: live.read,
			write: live.write
		)
	}
}

class Live {
	private var realm: Realm!
	private let queue: DispatchQueue

	init(queue: DispatchQueue) {
		self.queue = queue
		queue.sync {
			do {
				self.realm = try Realm(queue: queue)
			} catch {
				fatalError("Failed to open Realm")
			}
		}
	}

	@Sendable func read(_ block: (Realm) -> Void) {
		queue.sync {
			block(self.realm)
		}
	}

	@Sendable func write(_ block: @escaping (Realm) -> Void, _ onComplete: ((Error?) -> Void)?) {
		queue.async {
			self.realm.writeAsync({
				block(self.realm)
			}, onComplete: onComplete)
		}
	}
}
