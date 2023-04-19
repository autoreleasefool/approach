import AlleysRepositoryInterface
import ComposableArchitecture
import DateTimeLibrary
import EquatableLibrary
import ExtensionsLibrary
import FeatureActionLibrary
import FeatureFlagsLibrary
import FeatureFlagsServiceInterface
import FormLibrary
import Foundation
import LanesRepositoryInterface
import ModelsLibrary
import ResourcePickerLibrary
import SeriesRepositoryInterface
import StringsLibrary

public typealias SeriesForm = Form<Series.CreateWithLanes, Series.EditWithLanes>

extension Alley.Summary: PickableResource {
	static public func pickableModelName(forCount count: Int) -> String {
		count == 1 ? Strings.Alley.title : Strings.Alley.List.title
	}
}

extension Lane.Summary: PickableResource {
	static public func pickableModelName(forCount count: Int) -> String {
		count == 1 ? Strings.Lane.title : Strings.Lane.List.title
	}
}

public struct SeriesEditor: Reducer {
	public struct State: Equatable {
		public let hasAlleysEnabled: Bool
		public let hasLanesEnabled: Bool
		public let league: League.SeriesHost

		@BindingState public var numberOfGames: Int
		@BindingState public var date: Date
		@BindingState public var preBowl: Series.PreBowl
		@BindingState public var excludeFromStatistics: Series.ExcludeFromStatistics
		public var location: Alley.Summary?
		public var lanes: IdentifiedArrayOf<Lane.Summary>

		public let initialValue: SeriesForm.Value
		public var _form: SeriesForm.State

		public var alleyPicker: ResourcePicker<Alley.Summary, AlwaysEqual<Void>>.State
		public var isAlleyPickerPresented = false

		public var lanePicker: ResourcePicker<Lane.Summary, Alley.ID?>.State
		public var isLanePickerPresented = false

		public init(value: SeriesForm.Value, inLeague: League.SeriesHost) {
			self.league = inLeague
			self.initialValue = value
			self._form = .init(initialValue: value, currentValue: value)

			switch value {
			case let .create(new):
				self.numberOfGames = new.series.numberOfGames
				self.date = new.series.date
				self.preBowl = new.series.preBowl
				self.excludeFromStatistics = new.series.excludeFromStatistics
				self.location = new.series.location
				self.lanes = .init(uniqueElements: new.lanes)
			case let .edit(existing):
				self.numberOfGames = existing.series.numberOfGames
				self.date = existing.series.date
				self.preBowl = existing.series.preBowl
				self.excludeFromStatistics = existing.series.excludeFromStatistics
				self.location = existing.series.location
				self.lanes = .init(uniqueElements: existing.lanes)
			}

			self.alleyPicker = .init(
				selected: Set([self.location?.id].compactMap { $0 }),
				query: .init(()),
				limit: 1,
				showsCancelHeaderButton: false
			)
			self.lanePicker = .init(
				selected: Set(self.lanes.map(\.id)),
				query: self.location?.id,
				showsCancelHeaderButton: false
			)

			@Dependency(\.featureFlags) var featureFlags: FeatureFlagsService
			self.hasAlleysEnabled = featureFlags.isEnabled(.alleys)
			self.hasLanesEnabled = featureFlags.isEnabled(.lanes)
		}
	}

	public enum Action: FeatureAction, BindableAction, Equatable {
		public enum ViewAction: Equatable {
			case didAppear
			case setAlleyPicker(isPresented: Bool)
			case setLanePicker(isPresented: Bool)
		}
		public enum DelegateAction: Equatable {
			case didFinishEditing
		}
		public enum InternalAction: Equatable {
			case form(SeriesForm.Action)
			case alleyPicker(ResourcePicker<Alley.Summary, AlwaysEqual<Void>>.Action)
			case lanePicker(ResourcePicker<Lane.Summary, Alley.ID?>.Action)
		}

		case view(ViewAction)
		case `internal`(InternalAction)
		case delegate(DelegateAction)
		case binding(BindingAction<State>)
	}

	public init() {}

	@Dependency(\.alleys) var alleys
	@Dependency(\.date) var date
	@Dependency(\.dismiss) var dismiss
	@Dependency(\.lanes) var lanes
	@Dependency(\.series) var series
	@Dependency(\.uuid) var uuid

	public var body: some Reducer<State, Action> {
		BindingReducer()

		Scope(state: \.form, action: /Action.internal..Action.InternalAction.form) {
			SeriesForm()
				.dependency(\.records, .init(
					create: { (new: Series.CreateWithLanes) in try await series.create(new.series) },
					update: { (existing: Series.EditWithLanes) in try await series.update(existing.series) },
					delete: series.delete
				))
		}

		Scope(state: \.alleyPicker, action: /Action.internal..Action.InternalAction.alleyPicker) {
			ResourcePicker { _ in
				alleys.list(ordered: .byRecentlyUsed)
			}
		}

		Scope(state: \.lanePicker, action: /Action.internal..Action.InternalAction.lanePicker) {
			ResourcePicker { alley in
				lanes.list(alley)
			}
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didAppear:
					return .none

				case let .setAlleyPicker(isPresented):

					state.isAlleyPickerPresented = isPresented
					return .none

				case let .setLanePicker(isPresented):
					state.lanes = .init(uniqueElements: state.lanePicker.selectedResources ?? [])
					state.isLanePickerPresented = isPresented
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .alleyPicker(.delegate(delegateAction)):
					switch delegateAction {
					case .didFinishEditing:
						state.isAlleyPickerPresented = false
						state.location = state.alleyPicker.selectedResources?.first
						return state.lanePicker.updateQuery(to: state.location?.id)
							.map { .internal(.lanePicker($0)) }
					}

				case let .lanePicker(.delegate(delegateAction)):
					switch delegateAction {
					case .didFinishEditing:
						state.lanes = .init(uniqueElements: state.lanePicker.selectedResources ?? [])
						state.isLanePickerPresented = false
						return .none
					}

				case let .form(.delegate(delegateAction)):
					switch delegateAction {
					case let .didCreate(result):
						return state.form.didFinishCreating(result)
							.map { .internal(.form($0)) }

					case let .didUpdate(result):
						return state.form.didFinishUpdating(result)
							.map { .internal(.form($0)) }

					case let .didDelete(result):
						return state.form.didFinishDeleting(result)
							.map { .internal(.form($0)) }

					case .didFinishCreating, .didFinishUpdating, .didFinishDeleting, .didDiscard:
						return .fireAndForget { await self.dismiss() }
					}

				case .form(.view), .form(.internal):
					return .none

				case .alleyPicker(.internal), .alleyPicker(.view):
					return .none

				case .lanePicker(.internal), .lanePicker(.view):
					return .none
				}

			case .binding, .delegate:
				return .none
			}
		}
	}
}

extension Series.CreateWithLanes: CreateableRecord {
	public static var modelName = Strings.Series.title

	public var id: Series.ID { series.id }
	public var isSaveable: Bool { true }
	public var name: String { series.date.longFormat }
	public var saveButtonText: String { Strings.Action.start }
}

extension Series.EditWithLanes: EditableRecord {
	public var id: Series.ID { series.id }
	public var isDeleteable: Bool { true }
	public var isSaveable: Bool { true }
	public var name: String { series.date.longFormat }
}

extension SeriesEditor.State {
	var form: SeriesForm.State {
		get {
			var form = _form
			switch initialValue {
			case var .create(new):
				new.series.date = date
				new.series.preBowl = preBowl
				new.series.excludeFromStatistics = preBowl == .preBowl ? .exclude : excludeFromStatistics
				new.series.numberOfGames = numberOfGames
				new.series.location = location
				new.lanes = Array(lanes)
				form.value = .create(new)
			case var .edit(existing):
				existing.series.date = date
				existing.series.preBowl = preBowl
				existing.series.excludeFromStatistics = preBowl == .preBowl ? .exclude : excludeFromStatistics
				existing.series.location = location
				existing.lanes = Array(lanes)
				form.value = .edit(existing)
			}
			return form
		}
		set {
			_form = newValue
		}
	}
}
