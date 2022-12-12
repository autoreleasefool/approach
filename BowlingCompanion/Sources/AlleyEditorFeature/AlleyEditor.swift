import BaseFormFeature
import ComposableArchitecture
import Foundation
import LaneEditorFeature
import PersistenceServiceInterface
import SharedModelsLibrary
import StringsLibrary

extension Alley: BaseFormModel {
	static public var modelName = Strings.Alley.title
}

public struct AlleyEditor: ReducerProtocol {
	public typealias Form = BaseForm<Alley, Fields>

	public struct Fields: BaseFormState, Equatable {
		public var laneEditor: AlleyLanesEditor.State
		@BindableState public var name = ""
		@BindableState public var address = ""
		@BindableState public var material: Alley.Material = .unknown
		@BindableState public var pinFall: Alley.PinFall = .unknown
		@BindableState public var mechanism: Alley.Mechanism = .unknown
		@BindableState public var pinBase: Alley.PinBase = .unknown

		init(alley: Alley.ID?) {
			self.laneEditor = .init(alley: alley)
		}

		public let isDeleteable = true
		public var isSaveable: Bool {
			!name.isEmpty
		}
	}

	public struct State: Equatable {
		public var base: Form.State
		public var isLaneEditorPresented = false
		public var alleyLanes: AlleyLanes.State
		public let hasLanesEnabled: Bool

		public init(mode: Form.Mode, hasLanesEnabled: Bool) {
			var fields: Fields
			switch mode {
			case let .edit(alley):
				fields = .init(alley: alley.id)
				fields.name = alley.name
				fields.address = alley.address ?? ""
				fields.material = alley.material
				fields.pinFall = alley.pinFall
				fields.mechanism = alley.mechanism
				fields.pinBase = alley.pinBase
				self.alleyLanes = .init(alley: alley.id)
			case .create:
				fields = .init(alley: nil)
				self.alleyLanes = .init(alley: nil)
			}

			self.hasLanesEnabled = hasLanesEnabled
			self.base = .init(mode: mode, form: fields)
		}
	}

	public enum Action: BindableAction, Equatable {
		case setLaneEditor(isPresented: Bool)
		case binding(BindingAction<State>)
		case form(Form.Action)
		case alleyLanes(AlleyLanes.Action)
		case laneEditor(AlleyLanesEditor.Action)
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

		Scope(state: \.alleyLanes, action: /AlleyEditor.Action.alleyLanes) {
			AlleyLanes()
		}

		Scope(state: \.base.form.laneEditor, action: /AlleyEditor.Action.laneEditor) {
			AlleyLanesEditor()
		}

		Reduce { state, action in
			switch action {
			case let .setLaneEditor(isPresented):
				state.isLaneEditorPresented = isPresented
				return .none

			case .binding, .form, .alleyLanes, .laneEditor:
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
