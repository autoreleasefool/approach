import ComposableArchitecture
import ModelsLibrary
import ReorderingLibrary
import StatisticsWidgetsLibrary

extension StatisticsWidgetLayoutBuilder.State {
	var reordering: Reorderable<MoveableWidget, StatisticsWidget.Configuration>.State {
		get {
			var reordering = _reordering
			reordering.items = widgets
			return reordering
		}
		set {
			self.widgets = newValue.items
		}
	}
}
