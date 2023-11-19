import DatabaseModelsLibrary
import DatabaseServiceInterface
import Dependencies
import GRDB
import ModelsLibrary
import QuickLaunchRepositoryInterface
import RecentlyUsedServiceInterface
import RepositoryLibrary

extension QuickLaunchRepository: DependencyKey {
	public static var liveValue: Self = {
		return Self(
			defaultSource: {
				@Dependency(\.database) var database
				@Dependency(\.recentlyUsed) var recentlyUsed

				return asyncThrowingStream { continuation in
					do {
						for try await source in recentlyUsed.observeRecentlyUsedIds(.leagues)
							.map({ leagueIds -> QuickLaunchSource? in
							guard let leagueId = leagueIds.first else {
								return nil
							}

							return try await database.reader().read {
								let request = League.Database
									.filter(id: leagueId)
									.including(required: League.Database.bowler)
								return try QuickLaunchSource.fetchOne($0, request)
							}
						}) {
							continuation.yield(source)
						}
					} catch {
						continuation.finish(throwing: error)
					}
				}
			}
		)
	}()
}
