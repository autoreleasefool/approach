import DatabaseModelsLibrary
import DatabaseServiceInterface
import Dependencies
import GRDB
import LeaguesRepositoryInterface
import ModelsLibrary
import QuickLaunchRepositoryInterface
import RecentlyUsedServiceInterface
import RepositoryLibrary

extension QuickLaunchRepository: DependencyKey {
	public static var liveValue: Self {
		Self(
			defaultSource: {
				@Dependency(DatabaseService.self) var database
				@Dependency(RecentlyUsedService.self) var recentlyUsed
				@Dependency(LeaguesRepository.self) var leagues

				guard let recentBowlerId = recentlyUsed.getRecentlyUsed(.bowlers).first?.id else { return nil }
				for try await leagues in leagues.list(
					bowledBy: recentBowlerId,
					withRecurrence: .repeating,
					ordering: .byRecentlyUsed
				) {
					guard let league = leagues.first else { return nil }
					return try await database.reader().read {
						let request = League.Database
							.filter(id: league.id)
							.including(required: League.Database.bowler)
						return try QuickLaunchSource.fetchOne($0, request)
					}
				}

				return nil
			}
		)
	}
}
