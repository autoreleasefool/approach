import Dependencies
import FileManagerServiceInterface
import GRDB
import PersistenceServiceInterface

extension PersistenceService: DependencyKey {
	public static let liveValue: Self = {
		let dbManager: DatabaseManager
		do {
			try dbManager = DatabaseManager()
		} catch {
			// TODO: should notify user of failure to open DB
			fatalError("Unable to access persistence service, \(error)")
		}

		let modelPersistence = ModelPersistence(writer: dbManager.writer)
		let modelFetching = ModelFetching(reader: dbManager.reader)
		let modelQuerying = ModelQuerying(reader: dbManager.reader)

		return Self(
			createBowler: modelPersistence.create(model:),
			updateBowler: modelPersistence.update(model:),
			deleteBowler: modelPersistence.delete(model:),
			fetchBowlers: modelQuerying.fetchAll(request:),
			createLeague: modelPersistence.create(model:),
			updateLeague: modelPersistence.update(model:),
			deleteLeague: modelPersistence.delete(model:),
			fetchLeagues: modelFetching.fetchAll(request:),
			createSeries: modelPersistence.create(model:),
			updateSeries: modelPersistence.update(model:),
			deleteSeries: modelPersistence.delete(model:),
			fetchSeries: modelFetching.fetchAll(request:),
			createGame: modelPersistence.create(model:),
			updateGame: modelPersistence.update(model:),
			deleteGame: modelPersistence.delete(model:),
			fetchGames: modelFetching.fetchAll(request:),
			updateFrame: modelPersistence.update(model:),
			fetchFrames: modelQuerying.fetchAll(request:),
			createAlley: modelPersistence.create(model:),
			updateAlley: modelPersistence.update(model:),
			deleteAlley: modelPersistence.delete(model:),
			fetchAlleys: modelQuerying.fetchAll(request:)
		)
	}()
}
