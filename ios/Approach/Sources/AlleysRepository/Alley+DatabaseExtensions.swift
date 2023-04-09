import AlleysRepositoryInterface
import DatabaseModelsLibrary
import GRDB
import ModelsLibrary

extension Alley.Editable {
	var databaseModel: Alley.DatabaseModel {
		.init(
			id: id,
			name: name,
			address: address.isEmpty ? nil : address,
			material: material,
			pinFall: pinFall,
			mechanism: mechanism,
			pinBase: pinBase
		)
	}
}

extension Alley.Summary {
	init(_ model: Alley.DatabaseModel) {
		self.init(
			id: model.id,
			name: model.name,
			address: model.address,
			material: model.material,
			pinFall: model.pinFall,
			mechanism: model.mechanism,
			pinBase: model.pinBase
		)
	}
}

extension Alley.Editable {
	init?(_ model: Alley.DatabaseModel?) {
		guard let model else { return nil }
		self.init(
			id: model.id,
			name: model.name,
			address: model.address ?? "",
			material: model.material,
			pinFall: model.pinFall,
			mechanism: model.mechanism,
			pinBase: model.pinBase
		)
	}
}

extension DerivableRequest<Alley.DatabaseModel> {
	func orderByName() -> Self {
		let name = Alley.DatabaseModel.Columns.name
		return order(name.collating(.localizedCaseInsensitiveCompare))
	}

	func filter(byProperties properties: Alley.Filters) -> Self {
		var query = self
		if let material = properties.material {
			query = query.filter(Alley.DatabaseModel.Columns.material == material)
		}
		if let pinFall = properties.pinFall {
			query = query.filter(Alley.DatabaseModel.Columns.pinFall == pinFall)
		}
		if let mechanism = properties.mechanism {
			query = query.filter(Alley.DatabaseModel.Columns.mechanism == mechanism)
		}
		if let pinBase = properties.pinBase {
			query = query.filter(Alley.DatabaseModel.Columns.pinBase == pinBase)
		}
		return query
	}
}
