import BowlersRepositoryInterface
import DatabaseModelsLibrary
import GRDB
import ModelsLibrary

extension Bowler.Editable {
	var databaseModel: Bowler.DatabaseModel {
		.init(
			id: id,
			name: name,
			status: status
		)
	}
}

extension Bowler.Summary {
	init(_ model: Bowler.DatabaseModel) {
		self.init(id: model.id, name: model.name)
	}
}

extension Bowler.Editable {
	init?(_ model: Bowler.DatabaseModel?) {
		guard let model else { return nil }
		self.init(id: model.id, name: model.name, status: model.status)
	}
}

extension DerivableRequest<Bowler.DatabaseModel> {
	func orderByName() -> Self {
		let name = Bowler.DatabaseModel.Columns.name
		return order(name.collating(.localizedCaseInsensitiveCompare))
	}

	func filter(byStatus: Bowler.Status) -> Self {
		let status = Bowler.DatabaseModel.Columns.status
		return filter(status == byStatus)
	}
}
