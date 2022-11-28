import BaseFormFeature
import ComposableArchitecture
import Foundation
import PersistenceServiceInterface
import SharedModelsLibrary
import StringsLibrary

extension Alley: BaseFormModel {
	static public var modelName = Strings.Alleys.Model.name
}

public struct AlleyEditor: ReducerProtocol {
	public typealias Form = BaseForm<Alley, Fields>

	public struct Fields: BaseFormState, Equatable {
		@BindableState public var name = ""
		@BindableState public var address = ""
		@BindableState public var material: Alley.Material = .unknown
		@BindableState public var pinFall: Alley.PinFall = .unknown
		@BindableState public var mechanism: Alley.Mechanism = .unknown
		@BindableState public var pinBase: Alley.PinBase = .unknown

		public let isDeleteable = true
		public var isSaveable: Bool {
			!name.isEmpty
		}
	}

	public struct State: Equatable {
		public var base: Form.State

		public init(mode: Form.Mode) {
			var fields = Fields()
			if case let .edit(alley) = mode {
				fields.name = alley.name
				fields.address = alley.address ?? ""
				fields.material = alley.material
				fields.pinFall = alley.pinFall
				fields.mechanism = alley.mechanism
				fields.pinBase = alley.pinBase
			}

			self.base = .init(mode: mode, form: fields)
		}
	}

	public enum Action: BindableAction, Equatable {
		case binding(BindingAction<State>)
		case form(Form.Action)
	}

	public init() {}

	@Dependency(\.uuid) var uuid
	@Dependency(\.persistenceService) var persistenceService

	public var body: some ReducerProtocol<State, Action> {
		BindingReducer()

		Scope(state: \.base, action: /Action.form) {
			BaseForm()
				.dependency(\.modelPersistence, .init(
					create: persistenceService.createAlley,
					update: persistenceService.updateAlley,
					delete: persistenceService.deleteAlley
				))
		}

		Reduce { _, action in
			switch action {
			case .binding, .form:
				return .none
			}
		}
	}
}

extension AlleyEditor.Fields {
	public func model(fromExisting existing: Alley?) -> Alley {
		@Dependency(\.uuid) var uuid: UUIDGenerator

		return .init(
			id: existing?.id ?? uuid(),
			name: name,
			address: address.isEmpty ? nil : address,
			material: material,
			pinFall: pinFall,
			mechanism: mechanism,
			pinBase: pinBase
		)
	}
}
