import Dependencies
import Foundation
import PersistenceServiceInterface
import RealmSwift

extension PersistenceService: DependencyKey {
	public static let liveValue: Self = {
		class Wrapper {
			var realm: Realm!
			let queue: DispatchQueue

			init() {
				queue = DispatchQueue(label: "PersistenceService")
				queue.sync {
					do {
						realm = try Realm(queue: queue)
					} catch {
						fatalError("Failed to open Realm")
					}
				}
			}
		}

		let wrapper = Wrapper()

		return Self(
			read: { block in
				wrapper.queue.sync {
					block(wrapper.realm)
				}
			},
			write: { block, onComplete in
				wrapper.queue.async {
					wrapper.realm.writeAsync({
						block(wrapper.realm)
					}, onComplete: onComplete)
				}
			}
		)
	}()
}
