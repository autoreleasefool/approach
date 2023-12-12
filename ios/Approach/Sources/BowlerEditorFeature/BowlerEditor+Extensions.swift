import ComposableArchitecture

extension BowlerEditor.State {
	var form: BowlerForm.State {
		get {
			var form = _form
			switch initialValue {
			case var .create(new):
				new.name = name
				form.value = .create(new)
			case var .edit(existing):
				existing.name = name
				form.value = .edit(existing)
			}
			return form
		}
		set {
			_form = newValue
		}
	}
}
