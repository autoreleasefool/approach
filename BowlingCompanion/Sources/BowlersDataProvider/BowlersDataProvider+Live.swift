import BowlersDataProviderInterface
import PersistenceModelsLibrary
import PersistenceServiceInterface
import SharedModelsLibrary

extension BowlersDataProvider {
	public static func live(_ persistenceService: PersistenceService) -> Self {
		let live = Live(persistenceService: persistenceService)
		return .init(
			save: live.save,
			delete: live.delete,
			fetchAll: live.fetchAll
		)
	}
}

struct Live {
	let persistenceService: PersistenceService

	init(persistenceService: PersistenceService) {
		self.persistenceService = persistenceService
	}

	private func resumeOrThrow(_ error: Error?, continuation: CheckedContinuation<Void, Error>) {
		if let error {
			continuation.resume(throwing: error)
		} else {
			continuation.resume(returning: ())
		}
	}

	@Sendable func save(_ bowler: Bowler) async throws {
		try await withCheckedThrowingContinuation { (continuation: CheckedContinuation<Void, Error>) in
			persistenceService.write({ realm in
				realm.add(PersistentBowler(from: bowler))
			}, {
				resumeOrThrow($0, continuation: continuation)
			})
		}
	}

	@Sendable func delete(_ bowler: Bowler) async throws {
		try await withCheckedThrowingContinuation { (continuation: CheckedContinuation<Void, Error>) in
			persistenceService.write({ realm in
				if let persistent = realm.object(ofType: PersistentBowler.self, forPrimaryKey: bowler.id) {
					realm.delete(persistent)
				}
			}, {
				resumeOrThrow($0, continuation: continuation)
			})
		}
	}

	@Sendable func fetchAll() -> AsyncStream<[Bowler]> {
		.init { continuation in
			persistenceService.read {
				let bowlers = $0.objects(PersistentBowler.self)
				let token = bowlers.observe { _ in
					continuation.yield(bowlers.map { $0.bowler })
				}

				continuation.onTermination = { _ in
					token.invalidate()
				}
			}
		}
	}
}
