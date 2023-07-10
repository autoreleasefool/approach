import AlleysRepositoryInterface
import ComposableArchitecture
import DateTimeLibrary
import EquatableLibrary
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

public typealias SeriesForm = Form<Series.Create, Series.Edit>

public struct SeriesEditor: Reducer {
	public struct State: Equatable {
		public let hasAlleysEnabled: Bool
		public let league: League.SeriesHost

		public var numberOfGames: Int
		public var date: Date
		public var preBowl: Series.PreBowl
		public var excludeFromStatistics: Series.ExcludeFromStatistics
		public var location: Alley.Summary?

		public let initialValue: SeriesForm.Value
		public var _form: SeriesForm.State

		@PresentationState public var alleyPicker: ResourcePicker<Alley.Summary, AlwaysEqual<Void>>.State?

		public init(value: InitialValue, inLeague: League.SeriesHost) {
			self.league = inLeague
			switch value {
			case let .create(new):
				self.numberOfGames = new.numberOfGames
				self.date = new.date
				self.preBowl = new.preBowl
				self.excludeFromStatistics = new.excludeFromStatistics
				self.location = new.location
				self.initialValue = .create(new)
			case let .edit(existing):
				self.numberOfGames = existing.numberOfGames
				self.date = existing.date
				self.preBowl = existing.preBowl
				self.excludeFromStatistics = existing.excludeFromStatistics
				self.location = existing.location
				self.initialValue = .edit(existing)
			}
			self._form = .init(initialValue: self.initialValue, currentValue: self.initialValue)

			@Dependency(\.featureFlags) var featureFlags: FeatureFlagsService
			self.hasAlleysEnabled = featureFlags.isEnabled(.alleys)
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didTapAlley
			case didChangeDate(Date)
			case didChangeNumberOfGames(Int)
			case didChangePreBowl(Series.PreBowl)
			case didChangeExcludeFromStatistics(Series.ExcludeFromStatistics)
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
			case form(SeriesForm.Action)
			case alleyPicker(PresentationAction<ResourcePicker<Alley.Summary, AlwaysEqual<Void>>.Action>)
		}

		case view(ViewAction)
		case `internal`(InternalAction)
		case delegate(DelegateAction)
	}

	public enum InitialValue {
		case create(Series.Create)
		case edit(Series.Edit)
	}

	public init() {}

	@Dependency(\.alleys) var alleys
	@Dependency(\.date) var date
	@Dependency(\.dismiss) var dismiss
	@Dependency(\.series) var series
	@Dependency(\.uuid) var uuid

	public var body: some ReducerOf<Self> {
		Scope(state: \.form, action: /Action.internal..Action.InternalAction.form) {
			SeriesForm()
				.dependency(\.records, .init(
					create: series.create,
					update: series.update,
					delete: series.delete
				))
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didTapAlley:
					state.alleyPicker = .init(
						selected: Set([state.location?.id].compactMap { $0 }),
						query: .init(()),
						limit: 1,
						showsCancelHeaderButton: false
					)
					return .none

				case let .didChangeDate(date):
					state.date = date
					return .none

				case let .didChangePreBowl(preBowl):
					state.preBowl = preBowl
					return .none

				case let .didChangeNumberOfGames(numberOfGames):
					state.numberOfGames = numberOfGames
					return .none

				case let .didChangeExcludeFromStatistics(exclude):
					state.excludeFromStatistics = exclude
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .alleyPicker(.presented(.delegate(delegateAction))):
					switch delegateAction {
					case let .didChangeSelection(alley):
						state.location = alley.first
						return .none
					}

				case let .form(.delegate(delegateAction)):
					switch delegateAction {
					case let .didCreate(result):
						return state._form.didFinishCreating(result)
							.map { .internal(.form($0)) }

					case let .didUpdate(result):
						return state._form.didFinishUpdating(result)
							.map { .internal(.form($0)) }

					case let .didDelete(result):
						return state._form.didFinishDeleting(result)
							.map { .internal(.form($0)) }

					case .didFinishCreating, .didFinishUpdating, .didFinishDeleting, .didDiscard:
						return .run { _ in await self.dismiss() }
					}

				case .form(.view), .form(.internal):
					return .none

				case .alleyPicker(.presented(.internal)), .alleyPicker(.presented(.view)), .alleyPicker(.dismiss):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.$alleyPicker, action: /Action.internal..Action.InternalAction.alleyPicker) {
			ResourcePicker { _ in
				alleys.list(ordered: .byRecentlyUsed)
			}
		}
	}
}

extension SeriesEditor.State {
	var form: SeriesForm.State {
		get {
			var form = _form
			switch initialValue {
			case var .create(new):
				new.date = date
				new.preBowl = preBowl
				new.excludeFromStatistics = preBowl == .preBowl ? .exclude : excludeFromStatistics
				new.numberOfGames = numberOfGames
				new.location = location
				form.value = .create(new)
			case var .edit(existing):
				existing.date = date
				existing.preBowl = preBowl
				existing.excludeFromStatistics = preBowl == .preBowl ? .exclude : excludeFromStatistics
				existing.location = location
				form.value = .edit(existing)
			}
			return form
		}
		set {
			_form = newValue
		}
	}
}

extension Series.Create: CreateableRecord {
	public static var modelName = Strings.Series.title
	public static var isSaveableWithoutChanges: Bool { true }

	public var isSaveable: Bool { true }
	public var name: String { date.longFormat }
	public var saveButtonText: String { Strings.Action.start }
}

extension Series.Edit: EditableRecord {
	public var isDeleteable: Bool { true }
	public var isSaveable: Bool { true }
	public var name: String { date.longFormat }
}

extension Alley.Summary: PickableResource {
	static public func pickableModelName(forCount count: Int) -> String {
		count == 1 ? Strings.Alley.title : Strings.Alley.List.title
	}
}
