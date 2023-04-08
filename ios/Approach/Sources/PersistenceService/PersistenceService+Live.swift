import Dependencies
import FileManagerServiceInterface
import GRDB
import PersistenceServiceInterface

extension PersistenceService: DependencyKey {
	public static var liveValue: Self = {
		let dbManager: DatabaseManager
		do {
			try dbManager = DatabaseManager()
		} catch {
			// TODO: should notify user of failure to open DB
			fatalError("Unable to access persistence service, \(error)")
		}

		let modelPersistence = ModelPersistence(writer: dbManager.writer)
		let modelQuerying = ModelQuerying(reader: dbManager.reader)

		return Self(
			saveBowler: modelPersistence.save(model:),
			deleteBowler: modelPersistence.delete(model:),
			observeBowler: modelQuerying.observeOne(request:),
			observeBowlers: modelQuerying.observeAll(request:),
			saveLeague: modelPersistence.save(model:),
			deleteLeague: modelPersistence.delete(model:),
			observeLeagues: modelQuerying.observeAll(request:),
			saveSeries: modelPersistence.save(model:),
			deleteSeries: modelPersistence.delete(model:),
			observeSeries: modelQuerying.observeAll(request:),
			saveGame: modelPersistence.save(model:),
			deleteGame: modelPersistence.delete(model:),
			observeGames: modelQuerying.observeAll(request:),
			saveFrame: modelPersistence.save(model:),
			fetchFrames: modelQuerying.fetchAll(request:),
			saveAlley: modelPersistence.save(model:),
			deleteAlley: modelPersistence.delete(model:),
			observeAlley: modelQuerying.observeOne(request:),
			observeAlleys: modelQuerying.observeAll(request:),
			saveLanes: modelPersistence.save(models:),
			deleteLanes: modelPersistence.delete(models:),
			fetchLanes: modelQuerying.fetchAll(request:),
			observeLanes: modelQuerying.observeAll(request:),
			saveGear: modelPersistence.save(model:),
			deleteGear: modelPersistence.delete(model:),
			observeGear: modelQuerying.observeAll(request:),
			saveOpponent: modelPersistence.save(model:),
			deleteOpponent: modelPersistence.delete(model:),
			observeOpponents: modelQuerying.observeAll(request:)
		)
	}()
}
